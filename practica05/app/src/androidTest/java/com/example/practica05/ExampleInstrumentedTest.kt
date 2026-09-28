package com.example.practica05

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.practica05.data.PreferencesManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada: valida el ciclo completo de SharedPreferences
 * (guardar -> leer -> conservar -> borrar) sobre un dispositivo o emulador real.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        preferencesManager = PreferencesManager(context)
        preferencesManager.clearPreferencias()
    }

    @After
    fun tearDown() {
        preferencesManager.clearPreferencias()
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.practica05", appContext.packageName)
    }

    @Test
    fun guardarYRecuperarPreferencias() {
        preferencesManager.savePreferencias(
            nombre = "Alfar",
            notificaciones = true,
            modoOscuro = true
        )

        assertEquals("Alfar", preferencesManager.getNombre())
        assertTrue(preferencesManager.areNotificacionesActivas())
        assertTrue(preferencesManager.isModoOscuro())
    }

    @Test
    fun losDatosPersistenEnUnaNuevaInstanciaDelManager() {
        preferencesManager.savePreferencias(nombre = "Persistente", notificaciones = true)

        // Nueva instancia: simula reabrir la aplicacion.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nuevaInstancia = PreferencesManager(context)

        assertEquals("Persistente", nuevaInstancia.getNombre())
        assertTrue(nuevaInstancia.hayDatosGuardados())
    }

    @Test
    fun sinDatosGuardadosDevuelveValoresPorDefecto() {
        assertEquals("", preferencesManager.getNombre())
        assertFalse(preferencesManager.areNotificacionesActivas())
        assertFalse(preferencesManager.isModoOscuro())
        assertFalse(preferencesManager.hayDatosGuardados())
    }

    @Test
    fun borrarEliminaTodasLasLlaves() {
        preferencesManager.savePreferencias(nombre = "Temporal", notificaciones = true)

        assertTrue(preferencesManager.clearPreferencias())

        assertEquals("", preferencesManager.getNombre())
        assertFalse(preferencesManager.hayDatosGuardados())
    }
}
