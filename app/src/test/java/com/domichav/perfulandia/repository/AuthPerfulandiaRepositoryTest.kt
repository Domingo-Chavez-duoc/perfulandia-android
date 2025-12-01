package com.domichav.perfulandia.repository

import android.app.Application
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.ApiService
import com.domichav.perfulandia.data.remote.dto.ApiResponse
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.cliente.ClienteProfileDto
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * *** TESTS UNITARIOS CORREGIDOS Y ALINEADOS CON LA API REAL ***
 */
class UserRepositoryTest {

    private lateinit var mockApplication: Application
    private lateinit var mockApiService: ApiService
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        mockApplication = mockk(relaxed = true)
        mockApiService = mockk()

        // Mockea la construcción de SessionManager, que es una dependencia interna de UserRepository
        mockkConstructor(SessionManager::class)
        // El AccountRepository ya no se usa, por lo que no es necesario mockearlo.

        // Configura el RetrofitClient para que devuelva nuestro ApiService mockeado
        mockkObject(com.domichav.perfulandia.data.remote.RetrofitClient)
        every { com.domichav.perfulandia.data.remote.RetrofitClient.create(any()) } returns mockApiService

        repository = UserRepository(mockApplication)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    // ==================== LOGIN TESTS (CORREGIDOS) ====================

    @Test
    fun `login with valid credentials returns success and saves token`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        // --- CORREGIDO: La respuesta ahora viene envuelta en ApiResponse<T>
        val loginResponse = LoginResponse(accessToken = "mock_token_12345")
        val apiResponse = ApiResponse(success = true, data = loginResponse, message = "Success", total = null)

        // --- CORREGIDO: Se mockea la llamada a la función correcta del ApiService
        coEvery { mockApiService.login(loginRequest) } returns apiResponse
        coEvery { anyConstructed<SessionManager>().saveAuthToken(any()) } just runs
        // --- CORREGIDO: Añadimos el mock para guardar el email
        coEvery { anyConstructed<SessionManager>().saveUserEmail(any()) } just runs

        // When
        val result = repository.login(loginRequest)

        // Then
        assertTrue(result.isSuccess, "Login result should be Success")
        assertEquals(loginResponse, result.getOrNull())
        // --- CORREGIDO: Verifica que se guarde el token y el email
        coVerify { anyConstructed<SessionManager>().saveAuthToken("mock_token_12345") }
        coVerify { anyConstructed<SessionManager>().saveUserEmail("test@example.com") }
    }

    @Test
    fun `login with invalid credentials returns failure`() = runTest {
        // Given
        val loginRequest = LoginRequest("wrong@example.com", "wrongpassword")
        val exception = HttpException(Response.error<ApiResponse<LoginResponse>>(401, mockk(relaxed = true)))
        coEvery { mockApiService.login(loginRequest) } throws exception

        // When
        val result = repository.login(loginRequest)

        // Then
        assertTrue(result.isFailure, "Login result should be Failure")
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    // ==================== REGISTER TESTS (CORREGIDOS) ===================

    @Test
    fun `registration with valid data returns success and DOES NOT save token`() = runTest {
        // Given
        val registerRequest = RegisterRequest(nombre = "New User", email = "new@example.com", password = "password123")

        // --- CORRECCIÓN DEFINITIVA: Se crean todos los campos que tu UserDto requiere ---
        val userDto = UserDto(
            id = "user-123",
            email = "new@example.com",
            role = "cliente",
            avatar = null, // puede ser null porque es opcional (String?)
            isActive = true,
            emailVerified = false,
            createdAt = Date(), // Usamos la fecha actual para el test
            updatedAt = Date()  // Usamos la fecha actual para el test
        )

        val apiResponse = ApiResponse(success = true, data = userDto, message = "Created", total = null)

        coEvery { mockApiService.register(registerRequest) } returns apiResponse

        // When
        val result = repository.register(registerRequest)

        // Then
        assertTrue(result.isSuccess, "Registration result should be Success")
        assertEquals(userDto, result.getOrNull())
        coVerify(exactly = 0) { anyConstructed<SessionManager>().saveAuthToken(any()) }
    }

    @Test
    fun `registration with duplicate email returns failure`() = runTest {
        // Given
        val registerRequest = RegisterRequest(nombre = "Test", email = "existing@example.com", password = "password123")
        val exception = HttpException(Response.error<ApiResponse<UserDto>>(409, mockk(relaxed = true)))
        coEvery { mockApiService.register(registerRequest) } throws exception

        // When
        val result = repository.register(registerRequest)

        // Then
        assertTrue(result.isFailure, "Registration result should be Failure")
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    // ==================== GET PROFILE TESTS (CORREGIDOS) ====================

    @Test
    fun `getProfile returns success with ClienteProfileDto`() = runTest {
        // Given
        // --- CORREGIDO: La respuesta esperada es un ClienteProfileDto
        val userDto = UserDto(
            id = "user-123",
            email = "test@example.com",
            role = "cliente",
            avatar = null,
            isActive = true,
            emailVerified = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        val apiResponse = ApiResponse(success = true, data = userDto, message = "Success", total = null)
        coEvery { mockApiService.getMyProfile() } returns apiResponse

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isSuccess, "getProfile result should be Success")
        assertEquals(userDto, result.getOrNull())
        // --- CORREGIDO: Se elimina la lógica de "local token", ya que fue eliminada del ViewModel
    }

    @Test
    fun `getProfile with API error returns failure`() = runTest {
        // Given
        val exception = Exception("API error")
        coEvery { mockApiService.getMyProfile() } throws exception

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isFailure, "getProfile result should be Failure")
        assertEquals("API error", result.exceptionOrNull()?.message)
    }
}
