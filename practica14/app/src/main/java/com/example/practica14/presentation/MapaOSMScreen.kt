package com.example.practica14.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun MapaOSMScreen(viewModel: MapaViewModel = viewModel()) {

    val context = LocalContext.current
    val marcadores by viewModel.marcadores.collectAsState()
    val ubicacion by viewModel.ubicacion.collectAsState()

    var mapView by remember { mutableStateOf<MapView?>(null) }
    val markerOverlays = remember { mutableListOf<Marker>() }

    val permisos = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            viewModel.obtenerUbicacionActual()
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            viewModel.obtenerUbicacionActual()
        } else {
            launcher.launch(permisos)
        }
    }

    // Centrar la camara cuando se obtiene la ubicacion.
    LaunchedEffect(ubicacion) {
        val mv = mapView ?: return@LaunchedEffect
        val gp = ubicacion ?: return@LaunchedEffect
        mv.controller.animateTo(gp)
        mv.controller.setZoom(18.0)
    }

    // Refrescar marcadores persistidos sobre el mapa.
    LaunchedEffect(marcadores) {
        val mv = mapView ?: return@LaunchedEffect
        markerOverlays.forEach { mv.overlays.remove(it) }
        markerOverlays.clear()
        marcadores.forEach { m ->
            val marker = Marker(mv)
            marker.position = GeoPoint(m.latitud, m.longitud)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = m.titulo
            mv.overlays.add(marker)
            markerOverlays.add(marker)
        }
        mv.invalidate()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Practica 14 - GPS y Mapas (OSM)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(12.dp)
        )

        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
                Configuration.getInstance().userAgentValue = "Practica14/1.0 (Android; uso educativo)"

                MapView(ctx).apply {
                    // Fuente de teselas: mapa base libre (evita el bloqueo 403 de OSM).
                    val fuenteTeselas = object : org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase(
                        "EsriWorldStreetMap", 0, 19, 256, ".png",
                        arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
                    ) {
                        override fun getTileURLString(pMapTileIndex: Long): String {
                            val z = org.osmdroid.util.MapTileIndex.getZoom(pMapTileIndex)
                            val x = org.osmdroid.util.MapTileIndex.getX(pMapTileIndex)
                            val y = org.osmdroid.util.MapTileIndex.getY(pMapTileIndex)
                            return baseUrl + "$z/$y/$x"
                        }
                    }
                    setTileSource(fuenteTeselas)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)

                    // Overlay de eventos: long press para guardar marcador.
                    val eventsOverlay = MapEventsOverlay(object : org.osmdroid.events.MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean = false
                        override fun longPressHelper(p: GeoPoint): Boolean {
                            viewModel.guardarMarcador(p.latitude, p.longitude, "Marcador")
                            return true
                        }
                    })
                    overlays.add(eventsOverlay)

                    // Overlay de ubicacion en tiempo real con icono rojo personalizado.
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    val iconoRojo = drawableToBitmap(ctx, com.example.practica14.R.drawable.ic_location_red)
                    locationOverlay.setPersonIcon(iconoRojo)
                    locationOverlay.setDirectionArrow(iconoRojo, iconoRojo)
                    locationOverlay.enableMyLocation()
                    overlays.add(locationOverlay)

                    mapView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text(
            text = if (ubicacion != null)
                "Lat: ${ubicacion!!.latitude}, Lon: ${ubicacion!!.longitude}"
            else "Obteniendo ubicacion...",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}

private fun drawableToBitmap(context: Context, resId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, resId)!!
    if (drawable is android.graphics.drawable.BitmapDrawable) {
        return drawable.bitmap
    }
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
