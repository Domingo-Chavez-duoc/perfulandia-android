package com.domichav.perfulandia.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.domichav.perfulandia.data.remote.dto.LoginRequest // <-- IMPORTANTE
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepository: UserRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepository = mockk()
        viewModel = LoginViewModel(mockUserRepository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `onLoginClicked con exito debe poner isLoading a true y luego a false con loginSuccess a true`() = runTest {
        // Given
        // El mock espera cualquier objeto del tipo LoginRequest.
        coEvery { mockUserRepository.login(any<LoginRequest>()) } returns Result.success(mockk(relaxed = true))

        // When & Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.loginSuccess)

            viewModel.onLoginClicked("test@example.com", "password123")

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.loginSuccess)
            assertNull(successState.error)
        }

        coVerify { mockUserRepository.login(LoginRequest("test@example.com", "password123")) }
    }

    @Test
    fun `onLoginClicked con fallo debe poner isLoading a true y luego a false con un mensaje de error`() = runTest {
        // Given
        val exception = Exception("Credenciales inválidas")
        // --- CORRECCIÓN AQUÍ ---
        // Unificamos el mock para que espere un LoginRequest, igual que en el test de éxito.
        coEvery { mockUserRepository.login(any<LoginRequest>()) } returns Result.failure(exception)

        // When & Then
        viewModel.uiState.test {
            awaitItem() // Estado inicial
            viewModel.onLoginClicked("test@example.com", "wrongpassword")

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.loginSuccess)
            assertEquals("Credenciales inválidas", errorState.error)
        }

        // --- CORRECCIÓN AQUÍ ---
        coVerify { mockUserRepository.login(LoginRequest("test@example.com", "wrongpassword")) }
    }
}
