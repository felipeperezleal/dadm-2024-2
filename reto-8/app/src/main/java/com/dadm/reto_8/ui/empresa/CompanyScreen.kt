package com.dadm.reto_8.ui.empresa

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dadm.reto_8.data.model.Company
import com.dadm.reto_8.data.db.DatabaseHelper

@Composable
fun CompanyScreen(navController: NavController, context: Context) {
    val backgroundColor = Color(0xFF608C6C)
    val name = remember { mutableStateOf("") }
    val url = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val services = remember { mutableStateOf("") }
    var classification = remember { mutableStateOf("") }

    val classifications = listOf("Consultoría", "Desarrollo a la medida", "Fábrica de software")
    val isDropDownExpanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(backgroundColor)
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Nombre de la empresa") },
            )
            TextField(
                value = url.value,
                onValueChange = { url.value = it },
                label = { Text("URL") })
            TextField(
                value = phone.value,
                onValueChange = { phone.value = it },
                label = { Text("Teléfono") })
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") })
            TextField(
                value = services.value,
                onValueChange = { services.value = it },
                label = { Text("Productos y Servicios") })
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            isDropDownExpanded.value = true
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (classification.value.isEmpty()) "Clasificación de la empresa" else classification.value,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }
                ) {
                    classifications.forEach { classificationItem ->
                        DropdownMenuItem(
                            onClick = {
                                classification.value = classificationItem
                                isDropDownExpanded.value = false
                            },
                            text = { Text(text = classificationItem) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val dbHelper = DatabaseHelper(context)
                    val company = Company(
                        name = name.value,
                        url = url.value,
                        phone = phone.value,
                        email = email.value,
                        services = services.value,
                        classification = classification.value
                    )

                    dbHelper.insertCompany(company)

                    navController.popBackStack()
                }
            ) {
                Text("Guardar")
            }

            Button(onClick = { navController.popBackStack() }) {
                Text("Cancelar")
            }
        }
    }
}
