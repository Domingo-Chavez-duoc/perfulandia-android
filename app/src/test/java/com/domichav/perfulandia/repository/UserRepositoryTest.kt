package com.domichav.perfulandia.repository

import android.app.Application
import android.util.Log
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.ApiResponse
import com.domichav.perfulandia.data.remote.api.UserApiService
import com.domichav.perfulandia.data.remote.dto.*
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryTest {

    private lateinit var mockApplication: Application
    private lateinit var mockUserApiService: UserApiService
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        mockApplication = mockk(relaxed = true)
        mockUserApiService = mockk()

        // 1. Mockear las llamadas a Log para evitar errores en el entorno de test
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // 2. Mockear el constructor de SessionManager.
        mockkConstructor(SessionManager::class)

        // 3. Mockear RetrofitClient para que devuelva nuestro UserApiService mockeado
        mockkObject(RetrofitClient)
        every { RetrofitClient.createService(any(), UserApiService::class.java) } returns mockUserApiService

        // 4. Finalmente, crear el repositorio que usará los mocks anteriores
        repository = UserRepository(mockApplication)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `register con datos válidos debe retornar éxito`() = runTest {
        // Given
        val registerRequest = RegisterRequest(nombre = "New User", email = "new@example.com", password = "password123")
        val fakeUser = createUserDtoForTest(id = "user-123", email = "new@example.com")
        val apiResponse =
            ApiResponse(success = true, data = fakeUser, message = "Created", total = null)
        coEvery { mockUserApiService.register(registerRequest) } returns apiResponse

        // When
        val result = repository.register(registerRequest)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(fakeUser, result.getOrNull())
        // Verificar que NUNCA se guarde un token al registrarse
        coVerify(exactly = 0) { anyConstructed<SessionManager>().saveAuthToken(any()) }
    }

    @Test
    fun `getProfile debe retornar éxito con un UserDto`() = runTest {
        // Given
        val fakeUser = createUserDtoForTest("user-123", "test@example.com")
        val apiResponse =
            ApiResponse(success = true, data = fakeUser, message = "Success", total = null)
        coEvery { mockUserApiService.getMyProfile() } returns apiResponse

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isSuccess, "getProfile result should be Success")
        assertEquals(fakeUser, result.getOrNull())
    }

    @Test
    fun `getProfile con error de API debe retornar fallo`() = runTest {
        // Given
        val exception = Exception("API error")
        coEvery { mockUserApiService.getMyProfile() } throws exception

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isFailure)
        assertEquals("API error", result.exceptionOrNull()?.message)
    }

    // --- Función de ayuda que coincide con tu UserDto.kt ---
    private fun createUserDtoForTest(id: String, email: String): UserDto {
        return UserDto(
            id = id,
            email = email,
            role = "CLIENTE",
            avatar = null,
            isActive = true,
            emailVerified = false,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}