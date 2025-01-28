package com.dadm.reto_9

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@Composable
fun MapsActivity() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val mapView = MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        controller.setZoom(6.0)
        setMultiTouchControls(true)
        setBuiltInZoomControls(false)
    }

    Configuration.getInstance().userAgentValue = "com.dadm.reto_9/1.0"

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
        } else {
            val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    Log.d("Location", "location is found: $location")
                }
                .addOnFailureListener { exception ->
                    Log.d("Location", "Oops location failed with exception: $exception")
                }
        }
    }

    val universidadNacional = GeoPoint(4.63556, -74.082778)
    val museoNacional = GeoPoint(4.6155, -74.0683)
    val plazaBolivar = GeoPoint(4.59806, -74.0758)
    val monserrate = GeoPoint(4.605833, -74.056389)
    val centroInternacional = GeoPoint(4.6136147, -74.0705685675238)
    val corferias = GeoPoint(4.629746, -74.090166)
    val simonBolivar = GeoPoint(4.65805556, -74.09388889)
    val hospitalUnal = GeoPoint(4.64852385, -74.0961003770447)

    val markers = listOf(
        Marker(mapView).apply {
            position = universidadNacional
            title = "Universidad Nacional de Colombia"
            snippet = "Sede Bogotá"
            icon = createMaterialMarkerIcon(Color(0xFF6200EE)) // Color morado
        },
        Marker(mapView).apply {
            position = museoNacional
            title = "Museo Nacional de Colombia"
            snippet = "Museo de arte e historia"
            icon = createMaterialMarkerIcon(Color(0xFF03DAC6)) // Color turquesa
        },
        Marker(mapView).apply {
            position = plazaBolivar
            title = "Plaza de Bolívar"
            snippet = "Centro histórico de Bogotá"
            icon = createMaterialMarkerIcon(Color(0xFFFF0266)) // Color rosa
        },
        Marker(mapView).apply {
            position = monserrate
            title = "Monserrate"
            snippet = "Mirador y lugar turístico"
            icon = createMaterialMarkerIcon(Color(0xFF03A9F4)) // Color azul
        },
        Marker(mapView).apply {
            position = centroInternacional
            title = "Centro Internacional"
            snippet = "Zona comercial y empresarial"
            icon = createMaterialMarkerIcon(Color(0xFF4CAF50)) // Color verde
        },
        Marker(mapView).apply {
            position = corferias
            title = "Corferias"
            snippet = "Recinto Ferial Colombiano"
            icon = createMaterialMarkerIcon(Color(0xFFFF5722)) // Color naranja
        },
        Marker(mapView).apply {
            position = simonBolivar
            title = "Simón Bolivar"
            snippet = "Parque Urbano"
            icon = createMaterialMarkerIcon(Color(0xFFFFC107)) // Color amarillo
        },
        Marker(mapView).apply {
            position = hospitalUnal
            title = "Hospital Universitario Nacional de Colombia"
            snippet = "Hospital"
            icon = createMaterialMarkerIcon(Color(0xFF9C27B0)) // Color morado
        }
    )

    mapView.overlays.addAll(markers)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(56.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = Color.White,
        ) {
            IconButton(
                onClick = { mapView.controller.zoomIn() },
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Zoom In",
                    tint = Color.Black
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 80.dp, start = 16.dp)
                .size(56.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = Color.White,
        ) {
            IconButton(
                onClick = { mapView.controller.zoomOut() },
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_delete),
                    contentDescription = "Zoom Out",
                    tint = Color.Black
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = Color.White,
        ) {
            IconButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val geoPoint = GeoPoint(location.latitude, location.longitude)
                                mapView.controller.setCenter(geoPoint)
                                mapView.controller.setZoom(19.0)
                                mapView.controller.animateTo(geoPoint)
                            } else {
                                Toast.makeText(context, "Por favor, activa el GPS", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

fun createMaterialMarkerIcon(color: Color): android.graphics.drawable.Drawable {
    val size = 48
    val drawable = android.graphics.drawable.GradientDrawable()
    drawable.shape = android.graphics.drawable.GradientDrawable.OVAL
    drawable.setSize(size, size)
    drawable.setColor(color.toArgb())
    drawable.setStroke(4, android.graphics.Color.BLACK) // Borde negro
    return drawable
}