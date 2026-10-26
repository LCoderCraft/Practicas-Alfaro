package com.example.practica11.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class UbicacionAlmacenamiento(val etiqueta: String) {
    INTERNA("Memoria interna"),
    SDCARD("SDCard / externo")
}

data class OperacionArchivo(
    val exito: Boolean,
    val mensaje: String,
    val ruta: String = "",
    val contenido: String? = null
)

/**
 * Encapsula las operaciones de entrada/salida para que las pantallas de
 * Compose no trabajen directamente con File, streams ni con las rutas.
 */
object FileStorageManager {

    private const val CARPETA_EXTERNA = "documentos"
    private const val CARPETA_LEGADO = "practica11"

    fun nombreSeguro(nombre: String): String {
        val limpio = nombre.trim().replace(Regex("""[\\/:*?"<>|]"""), "_")
        if (limpio.isBlank() || limpio == "." || limpio == "..") return "nota.txt"
        val conExtension = if (limpio.contains('.')) limpio else "$limpio.txt"
        return conExtension.take(80)
    }

    fun rutaInterna(context: Context, nombre: String): File =
        File(context.filesDir, nombreSeguro(nombre))

    fun rutaSdCard(context: Context, nombre: String): File? {
        val carpeta = carpetaSdCard(context) ?: return null
        return File(carpeta, nombreSeguro(nombre))
    }

    fun rutaPara(
        context: Context,
        ubicacion: UbicacionAlmacenamiento,
        nombre: String
    ): File? = when (ubicacion) {
        UbicacionAlmacenamiento.INTERNA -> rutaInterna(context, nombre)
        UbicacionAlmacenamiento.SDCARD -> rutaSdCard(context, nombre)
    }

    /**
     * En Android 9 o inferior se usa la carpeta publica Documentos, que es la
     * que exige los permisos READ/WRITE_EXTERNAL_STORAGE. En Android 10+ se usa
     * la carpeta privada de la app dentro del volumen externo (la SDCard si el
     * equipo tiene una), por lo que no se necesitan permisos.
     */
    @Suppress("DEPRECATION")
    fun carpetaSdCard(context: Context): File? {
        val carpeta = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                CARPETA_LEGADO
            )
        } else {
            val directorios = context.getExternalFilesDirs(null)?.filterNotNull().orEmpty()
            val removible = directorios.firstOrNull {
                Environment.isExternalStorageRemovable(it)
            }
            val base = removible
                ?: directorios.firstOrNull()
                ?: context.getExternalFilesDir(null)
                ?: return null
            File(base, CARPETA_EXTERNA)
        }
        if (!carpeta.exists()) carpeta.mkdirs()
        return carpeta
    }

    suspend fun guardar(
        context: Context,
        ubicacion: UbicacionAlmacenamiento,
        nombre: String,
        contenido: String
    ): OperacionArchivo = withContext(Dispatchers.IO) {
        try {
            val archivo = rutaPara(context, ubicacion, nombre)
                ?: return@withContext OperacionArchivo(
                    false,
                    "No se encontro la tarjeta SD ni el almacenamiento externo."
                )

            archivo.parentFile?.mkdirs()
            FileOutputStream(archivo).use { flujo ->
                OutputStreamWriter(flujo, Charsets.UTF_8).use { escritor ->
                    escritor.write(contenido)
                }
            }
            OperacionArchivo(
                exito = true,
                mensaje = "Archivo guardado correctamente.",
                ruta = archivo.absolutePath
            )
        } catch (e: SecurityException) {
            OperacionArchivo(false, "No hay permisos para escribir en esa ubicacion.")
        } catch (e: Exception) {
            OperacionArchivo(false, "Error al guardar: ${e.message ?: "desconocido"}.")
        }
    }

    suspend fun leer(
        context: Context,
        ubicacion: UbicacionAlmacenamiento,
        nombre: String
    ): OperacionArchivo = withContext(Dispatchers.IO) {
        try {
            val archivo = rutaPara(context, ubicacion, nombre)
                ?: return@withContext OperacionArchivo(
                    false,
                    "No se encontro la tarjeta SD ni el almacenamiento externo."
                )

            if (!archivo.exists()) {
                return@withContext OperacionArchivo(
                    false,
                    "El archivo no existe en ${ubicacion.etiqueta.lowercase()}.",
                    archivo.absolutePath
                )
            }

            val texto = FileInputStream(archivo).use { flujo ->
                BufferedReader(InputStreamReader(flujo, Charsets.UTF_8)).use { lector ->
                    lector.readText()
                }
            }
            OperacionArchivo(
                exito = true,
                mensaje = "Archivo leido correctamente.",
                ruta = archivo.absolutePath,
                contenido = texto
            )
        } catch (e: SecurityException) {
            OperacionArchivo(false, "No hay permisos para leer en esa ubicacion.")
        } catch (e: Exception) {
            OperacionArchivo(false, "Error al leer: ${e.message ?: "desconocido"}.")
        }
    }

    suspend fun eliminar(
        context: Context,
        ubicacion: UbicacionAlmacenamiento,
        nombre: String
    ): OperacionArchivo = withContext(Dispatchers.IO) {
        try {
            val archivo = rutaPara(context, ubicacion, nombre)
                ?: return@withContext OperacionArchivo(
                    false,
                    "No se encontro la tarjeta SD ni el almacenamiento externo."
                )

            if (!archivo.exists()) {
                return@withContext OperacionArchivo(
                    false,
                    "El archivo no existe en ${ubicacion.etiqueta.lowercase()}.",
                    archivo.absolutePath
                )
            }
            if (archivo.delete()) {
                OperacionArchivo(true, "Archivo eliminado.", archivo.absolutePath)
            } else {
                OperacionArchivo(false, "No se pudo eliminar el archivo.", archivo.absolutePath)
            }
        } catch (e: Exception) {
            OperacionArchivo(false, "Error al eliminar: ${e.message ?: "desconocido"}.")
        }
    }

    // ---------------------------------------------------------------------
    // Permisos
    // ---------------------------------------------------------------------

    fun requierePermisosLegados(): Boolean =
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

    fun permisosLegados(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun tienePermisos(context: Context): Boolean {
        if (!requierePermisosLegados()) return true
        return permisosLegados().all { permiso ->
            ContextCompat.checkSelfPermission(context, permiso) ==
                PackageManager.PERMISSION_GRANTED
        }
    }

    fun estadoPermisos(context: Context): String = when {
        !requierePermisosLegados() ->
            "No se requieren permisos en Android ${Build.VERSION.RELEASE}."
        tienePermisos(context) -> "Permisos de lectura y escritura concedidos."
        else -> "Permisos de lectura y escritura pendientes."
    }
}
