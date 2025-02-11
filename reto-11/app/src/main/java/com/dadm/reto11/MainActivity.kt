package com.dadm.reto11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dadm.reto11.ui.theme.Reto11Theme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto11Theme {
                Prompt()
            }
        }
    }
}

@Composable
fun Prompt(
    modifier: Modifier = Modifier,
    viewModel: GeminiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp)

    ) {
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ingrese su prompt") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.generateContent(prompt) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar respuesta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is GeminiUiState.Initial -> {
                Text("Ingrese un prompt para comenzar")
            }
            is GeminiUiState.Loading -> {
                CircularProgressIndicator()
            }
            is GeminiUiState.Success -> {
                Text((uiState as GeminiUiState.Success).response)
            }
            is GeminiUiState.Error -> {
                Text(
                    text = (uiState as GeminiUiState.Error).error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}