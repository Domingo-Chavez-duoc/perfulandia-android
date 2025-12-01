package com.domichav.perfulandia.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.domichav.perfulandia.ui.theme.PerfulandiaTheme
import com.domichav.perfulandia.viewmodel.LoginUiState
import com.domichav.perfulandia.viewmodel.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val emailFieldTag = "EmailField"
    private val passwordFieldTag = "PasswordField"
    private val loginButtonTag = "LoginButton"

    // 1. Mock del ViewModel
    private val mockLoginViewModel: LoginViewModel = mockk(relaxed = true)

    @Before
    fun setUp() {
        // 2. Definir un estado por defecto para el uiState del ViewModel
        every { mockLoginViewModel.uiState } returns MutableStateFlow(LoginUiState())

        // 3. Establecer el contenido de la prueba con el ViewModel mockeado
        composeTestRule.setContent {
            PerfulandiaTheme {
                LoginScreen(
                    navController = rememberNavController(),
                    loginViewModel = mockLoginViewModel // Inyectar el mock
                )
            }
        }
    }

    @Test
    fun loginButton_desabilitado_inicialmente() {
        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }

    @Test
    fun loginButton_abilitado_cuando_Form_validado() {
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("password123")

        composeTestRule.onNodeWithTag(loginButtonTag).assertIsEnabled()
    }

    @Test
    fun loginButton_desabilitado_cuando_email_invalido() {
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("email-invalido")

        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("password123")

        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }

    @Test
    fun loginButton_desabilitado_cuando_contrasena_corta() {
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("123")

        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }
}
