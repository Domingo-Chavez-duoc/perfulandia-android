package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.cash.turbine.test
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.repository.UserRepository
import io.mockk.coEvery
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para LoginViewModel.
 * Adaptado para la implementación real del proyecto.
 *
 * Cubre:
 * - Login exitoso y el cambio de estado de la UI (Loading -> Success).
 * - Login fallido y el manejo de errores (Loading -> Error).
 * - Correcta gestión del estado de carga (isLoading).
 */

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // Regla para ejecutar tareas de LiveData/ViewModel de forma síncrona
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Dispatcher de prueba para controlar las coroutines
    private val testDispatcher = StandardTestDispatcher()

    // Mocks: creamos versiones falsas de las dependencias
    private lateinit var mockApplication: Application
    private lateinit var mockUserRepository: UserRepository

    // SUT (System Under Test): la clase que estamos probando
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        // Configurar el dispatcher de prueba para las coroutines
        Dispatchers.setMain(testDispatcher)

        // Crear los mocks necesarios
        mockApplication = mockk(relaxed = true)
        mockUserRepository = mockk(relaxed = true) // 'relaxed' para no tener que definir todos sus métodos

        // --- Inyección de Dependencias Manual para el Test ---
        // Como LoginViewModel crea su propio UserRepository, no podemos inyectar el mock
        // directamente. Usamos una Factory para sobreescribir la creación del ViewModel
        // y así poder pasarle nuestro mockRepository.
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(mockApplication).apply {
                    // Reemplazamos el repositorio real por nuestro mock
                    injectUserRepositoryForTest(mockUserRepository)
                } as T
            }
        }
        // Creamos el viewModel usando la factory
        viewModel = viewModelFactory.create(LoginViewModel::class.java)
    }

    @After
    fun teardown() {
        // Limpiar el dispatcher y los mocks después de cada prueba
        Dispatchers.resetMain()
        unmockkAll()
    }

    // ==================== LOGIN TESTS ====================

    @Test
    fun `onLoginClicked con exito debe poner isLoading a true y luego a false con loginSuccess a true`() = runTest {
        // Given (Dado que...)
        // Simulamos una respuesta exitosa del repositorio
        val fakeSuccessResponse = LoginResponse(authToken = "fake-jwt-token", accessToken = null)
        coEvery { mockUserRepository.login(any<LoginRequest>()) } returns Result.success(fakeSuccessResponse)

        // When (Cuando...) y Then (Entonces...)
        viewModel.uiState.test {
            // 1. Verificar el estado inicial
            val initialState = awaitItem()
            assertFalse(initialState.isLoading, "Inicialmente, isLoading debe ser false")
            assertFalse(initialState.loginSuccess, "Inicialmente, loginSuccess debe ser false")
            assertNull(initialState.error, "Inicialmente, error debe ser null")

            // 2. Llamar a la función a probar
            viewModel.onLoginClicked("test@example.com", "password123")

            // 3. Verificar el estado de carga (Loading)
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading, "isLoading debe ser true durante la llamada")
            assertNull(loadingState.error)

            // 4. Verificar el estado final (Success) tras la coroutine
            val successState = awaitItem()
            assertFalse(successState.isLoading, "Al finalizar, isLoading debe ser false")
            assertTrue(successState.loginSuccess, "loginSuccess debe ser true en caso de éxito")
            assertNull(successState.error)
        }
    }

    @Test
    fun `onLoginClicked con fallo debe poner isLoading a true y luego a false con un mensaje de error`() = runTest {
        // Given (Dado que...)
        // Simulamos una respuesta fallida del repositorio
        val exception = Exception("Credenciales inválidas")
        coEvery { mockUserRepository.login(any<LoginRequest>()) } returns Result.failure(exception)

        // When (Cuando...) y Then (Entonces...)
        viewModel.uiState.test {
            // 1. Saltamos el estado inicial
            awaitItem()

            // 2. Llamar a la función
            viewModel.onLoginClicked("test@example.com", "wrongpassword")

            // 3. Verificar el estado de carga
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // 4. Verificar el estado de error
            val errorState = awaitItem()
            assertFalse(errorState.isLoading, "isLoading debe volver a ser false tras el error")
            assertFalse(errorState.loginSuccess, "loginSuccess debe ser false en caso de error")
            assertNotNull(errorState.error, "El mensaje de error no debe ser null")
            assertEquals("Credenciales inválidas", errorState.error)
        }
    }
}
