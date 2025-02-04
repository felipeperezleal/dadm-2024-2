package com.dadm.reto_10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dadm.reto_10.ui.theme.Reto10Theme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuntosPosconsumoApp()
        }
    }
}

@Composable
fun PuntosPosconsumoApp(viewModel: PuntosPosconsumoViewModel = viewModel()) {
    val puntosPosconsumo by viewModel.puntosPosconsumo.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { viewModel.fetchPuntosPosconsumo() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cargar Puntos de Posconsumo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(puntosPosconsumo) { punto ->
                PuntoPosconsumoItem(punto)
            }
        }
    }
}


@Composable
fun PuntoPosconsumoItem(punto: PuntoPosconsumo) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(200.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Año de registro: ${punto.a_o_a_o_de_registro}")
            Text(text = "Autoridad ambiental: ${punto.autoridad_ambiental_donde}")
            Text(text = "Región: ${punto.regi_n_donde_se_encuentra}")
            Text(text = "Departamento: ${punto.departamento_donde_se}")
            Text(text = "Municipio: ${punto.municipio_donde_se_encuentra}")
            Text(text = "Razón social: ${punto.raz_n_social_del_negocio}")

            Text(
                text = "Descripción: ${punto.descripci_n_del_negocio_verde}",
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(text = "Categoría: ${punto.categor_a_del_negocio_verde}")
            Text(text = "Sector: ${punto.sector_al_cual_pertenece}")
            Text(text = "Subsector: ${punto.subsector_al_cual_pertenece}")
            Text(text = "Producto principal: ${punto.producto_principal_que}")
            Text(text = "Categoría comercial: ${punto.categor_a_comercial_al_cual}")
            Text(text = "Representante: ${punto.nombre_representante_del}")

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Mostrar menos" else "Mostrar más")
            }
        }
    }
}