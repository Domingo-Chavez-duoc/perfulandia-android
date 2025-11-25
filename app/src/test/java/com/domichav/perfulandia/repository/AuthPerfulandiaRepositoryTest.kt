package com.domichav.perfulandia.repository

import android.app.Application
import com.domichav.perfulandia.data.local.Account
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.api.AuthApiService
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.data.remote.dto.UserResponse
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryTest {

    private lateinit var mockApplication: Application
    private lateinit var mockApiService: AuthApiService

    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        mockApplication = mockk(relaxed = true)
        mockApiService = mockk()

        // Mock constructors for dependencies created inside UserRepository
        mockkConstructor(SessionManager::class)
        mockkConstructor(AccountRepository::class)

        mockkObject(com.domichav.perfulandia.data.remote.RetrofitClient)
        every { com.domichav.perfulandia.data.remote.RetrofitClient.create(any()) } returns mockApiService

        repository = UserRepository(mockApplication)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    // ==================== LOGIN TESTS ====================

    @Test
    fun `login with valid credentials returns success and saves token`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val loginResponse = LoginResponse(authToken = "mock_token_12345", accessToken = null)

        coEvery { mockApiService.login(loginRequest) } returns loginResponse
        coEvery { anyConstructed<SessionManager>().saveAuthToken(any()) } just runs
        every { anyConstructed<SessionManager>().authToken } returns flowOf("mock_token_12345")

        // When
        val result = repository.login(loginRequest)

        // Then
        assertTrue(result.isSuccess, "Login result should be Success")
        assertEquals(loginResponse, result.getOrNull())
        coVerify { anyConstructed<SessionManager>().saveAuthToken("mock_token_12345") }
    }

    @Test
    fun `login with invalid credentials returns failure`() = runTest {
        // Given
        val loginRequest = LoginRequest("wrong@example.com", "wrongpassword")
        val exception = HttpException(Response.error<LoginResponse>(401, mockk(relaxed = true)))

        coEvery { mockApiService.login(loginRequest) } throws exception

        // When
        val result = repository.login(loginRequest)

        // Then
        assertTrue(result.isFailure, "Login result should be Failure")
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    // ==================== REGISTER TESTS ===================

    @Test
    fun `registration with valid data returns success and saves token`() = runTest {
        // Given
        val signupRequest = SignupRequest(name = "New User", email = "new@example.com", password = "password123")
        val signupResponse = SignupResponse(authToken = "new_token_12345", accessToken = null)

        coEvery { mockApiService.signup(signupRequest) } returns signupResponse
        coEvery { anyConstructed<SessionManager>().saveAuthToken(any()) } just runs
        every { anyConstructed<SessionManager>().authToken } returns flowOf("new_token_12345")

        // When
        val result = repository.register(signupRequest)

        // Then
        assertTrue(result.isSuccess, "Registration result should be Success")
        assertEquals(signupResponse, result.getOrNull())
        coVerify { anyConstructed<SessionManager>().saveAuthToken("new_token_12345") }
    }

    @Test
    fun `registration with duplicate email returns failure`() = runTest {
        // Given
        val signupRequest = SignupRequest(name = "Test", email = "existing@example.com", password = "password123")
        val exception = HttpException(Response.error<SignupResponse>(409, mockk(relaxed = true)))

        coEvery { mockApiService.signup(signupRequest) } throws exception

        // When
        val result = repository.register(signupRequest)

        // Then
        assertTrue(result.isFailure, "Registration result should be Failure")
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    // ==================== GET PROFILE TESTS ====================

    @Test
    fun `getProfile with remote token returns success`() = runTest {
        // Given
        val userResponse = UserResponse(id = 1, name = "Test User", email = "test@example.com")
        every { anyConstructed<SessionManager>().authToken } returns flowOf("some_remote_token")
        coEvery { mockApiService.getMe() } returns userResponse

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isSuccess, "getProfile result should be Success")
        assertEquals(userResponse, result.getOrNull())
    }

    @Test
    fun `getProfile with local token returns success`() = runTest {
        // Given
        val email = "local@example.com"
        every { anyConstructed<SessionManager>().authToken } returns flowOf("local-token-$email")

        val mockAccount = Account(name = "Local User", email = email, password = "")
        coEvery { anyConstructed<AccountRepository>().getAllAccountsOnce() } returns listOf(mockAccount)

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isSuccess, "getProfile with local token should be Success")
        val userResponse = result.getOrNull()
        assertEquals("Local User", userResponse?.name)
        assertEquals(email, userResponse?.email)
    }

    @Test
    fun `getProfile with API error returns failure`() = runTest {
        // Given
        val exception = Exception("API error")
        every { anyConstructed<SessionManager>().authToken } returns flowOf("some_remote_token")
        coEvery { mockApiService.getMe() } throws exception

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isFailure, "getProfile result should be Failure")
        assertEquals("API error", result.exceptionOrNull()?.message)
    }
}
