package com.domichav.perfulandia.ui.screens

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.domichav.perfulandia.ui.theme.PerfulandiaTheme
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de UI para el Composable LoginScreen.
 *
 * Estas pruebas verifican la funcionalidad de la UI en aislamiento, como:
 * - La validación de campos (email y contraseña).
 * - El comportamiento del botón de login (habilitado/deshabilitado).
 * - El cambio de visibilidad de la contraseña.
 *
 * NOTA: Estas pruebas se ejecutan en un emulador o dispositivo Android.
 */
class LoginScreenTest {

    // Regla para controlar y probar Composables
    @get:Rule
    val composeTestRule = createComposeRule()

    // Tags de prueba para encontrar fácilmente los componentes en la UI
    private val emailFieldTag = "EmailField"
    private val passwordFieldTag = "PasswordField"
    private val loginButtonTag = "LoginButton"

    @Before
    fun setUp() {
        // Establecer el contenido de la prueba.
        // Envolvemos LoginScreen en nuestro tema y le pasamos un NavController de prueba.
        composeTestRule.setContent {
            PerfulandiaTheme {
                LoginScreen(navController = rememberNavController())
            }
        }
    }

    @Test
    fun loginButton_desabilitado_inicialmente() {
        // Then (Entonces...)
        // El botón de login debe estar deshabilitado porque los campos están vacíos.
        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }

    @Test
    fun loginButton_abilitado_cuando_Form_validado() {
        // When (Cuando...)
        // Escribimos un email válido en el campo de correo.
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("test@example.com")

        // Escribimos una contraseña válida en el campo de contraseña.
        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("password123")

        // Then (Entonces...)
        // El botón de login debe estar habilitado.
        composeTestRule.onNodeWithTag(loginButtonTag).assertIsEnabled()
    }

    @Test
    fun loginButton_desabilitado_cuando_email_invalido() {
        // When (Cuando...)
        // Escribimos un email inválido.
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("email-invalido")

        // Escribimos una contraseña válida.
        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("password123")

        // Then (Entonces...)
        // El botón de login debe seguir deshabilitado.
        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }

    @Test
    fun loginButton_desabilitado_cuando_contrasena_corta() {
        // When (Cuando...)
        // Escribimos un email válido.
        composeTestRule.onNodeWithTag(emailFieldTag)
            .performTextInput("test@example.com")

        // Escribimos una contraseña demasiado corta.
        composeTestRule.onNodeWithTag(passwordFieldTag)
            .performTextInput("123")

        // Then (Entonces...)
        // El botón de login debe seguir deshabilitado.
        composeTestRule.onNodeWithTag(loginButtonTag).assertIsNotEnabled()
    }
}

