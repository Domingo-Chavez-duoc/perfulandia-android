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

    @Test
    fun visibilidad_password_cambia_cuando_icono_clickeado() {
        // Given (Dado que...)
        // Escribimos algo en el campo de contraseña.
        val testPassword = "secret-password"
        val passwordNode = composeTestRule.onNodeWithTag(passwordFieldTag)
        passwordNode.performTextInput("secret-password")

        // Then (Entonces...)
        // Verificamos que inicialmente la contraseña está oculta (por defecto).
        // `assertIsPassword()` es un atajo para esto.
        val initialText = passwordNode.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
        Assert.assertNotEquals(testPassword, initialText?.toString(), "La contraseña no debería ser visible inicialmente")



        // When (Cuando...)
        // Hacemos clic en el icono del ojo (trailing icon).
        passwordNode.onChild().performClick() // El icono es el único hijo del OutlinedTextField

        // Then (Entonces...)
        // Verificamos que la contraseña ahora es visible.
        passwordNode.assertTextEquals(testPassword)

        // When (Cuando...)
        // Hacemos clic en el icono de nuevo.
        passwordNode.onChild().performClick()

        // Then (Entonces...)
        // Verificamos que la contraseña se oculta de nuevo.
        val finalText = passwordNode.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
        Assert.assertNotEquals(testPassword, finalText?.toString(), "La contraseña debería estar oculta de nuevo")
    }
}

