package com.example.holamundointeractivo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun useAppContext() {
        // Escribe un nombre y presiona el boton para verificar el saludo
        composeTestRule.onNodeWithText("Escribe tu nombre").performTextInput("Ana")
        composeTestRule.onNodeWithText("Saludar").performClick()
        composeTestRule.onNodeWithText("¡Hola, Ana! Bienvenido al mundo de Android.")
    }
}