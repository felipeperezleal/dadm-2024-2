package com.dadm.reto_8

import android.content.Context
import android.widget.SearchView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dadm.reto_8.data.db.DatabaseHelper
import com.dadm.reto_8.data.model.Company

@Composable
fun HomeScreen(navController: NavController, context: Context) {
    val backgroundColor = Color(0xFF608C6C)
    var searchText by remember { mutableStateOf("") }
    val classificationFilter = remember { mutableStateOf("") }
    val dbHelper = DatabaseHelper(context)
    val companies = dbHelper.getAllCompanies()
    val filteredCompanies = companies.filter {
        it.name.contains(searchText, ignoreCase = true) &&
                (classificationFilter.value.isEmpty() || it.classification.contains(classificationFilter.value, ignoreCase = true))
    }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor).padding(18.dp)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
        ) {
            Text(
                text = "CRUD - Reto 8",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenuFilter(classificationFilter = classificationFilter)

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar por nombre") },
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(color = backgroundColor),
                    shape = RoundedCornerShape(8.dp),
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredCompanies, key = { company -> company.name }) { company ->
                        CompanyCard(company = company, onClick = {
                            selectedCompany = company
                            showDialog = true
                        })
                    }
                }
            }

        }

        FloatingActionButton(
            onClick = { navController.navigate("companyScreen") },
            containerColor = Color.White,
            contentColor = backgroundColor,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Add, "Floating action button")
        }

        if (showDialog && selectedCompany != null) {
            ShowCompanyDetailsDialog(
                company = selectedCompany!!,
                onDismiss = { showDialog = false },
                onDelete = { company ->
                    dbHelper.deleteCompany(company)
                    showDialog = false
                },
                onUpdate = { company ->
                    dbHelper.updateCompany(company)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CompanyCard(company: Company, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = company.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Clasificación: ${company.classification}")
            Text(text = "Email: ${company.email}")
            Text(text = "Teléfono: ${company.phone}")
            Text(text = "Servicios: ${company.services}")
        }
    }
}

@Composable
fun ShowCompanyDetailsDialog(
    company: Company,
    onDismiss: () -> Unit,
    onDelete: (Company) -> Unit,
    onUpdate: (Company) -> Unit
) {
    var updatedCompany by remember { mutableStateOf(company) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles de la empresa") },
        text = {
            Column {
                TextField(
                    value = updatedCompany.name,
                    onValueChange = { updatedCompany = updatedCompany.copy(name = it) },
                    label = { Text("Nombre") }
                )
                TextField(
                    value = updatedCompany.classification,
                    onValueChange = { updatedCompany = updatedCompany.copy(classification = it) },
                    label = { Text("Clasificación") }
                )
                TextField(
                    value = updatedCompany.email,
                    onValueChange = { updatedCompany = updatedCompany.copy(email = it) },
                    label = { Text("Email") }
                )
                TextField(
                    value = updatedCompany.phone,
                    onValueChange = { updatedCompany = updatedCompany.copy(phone = it) },
                    label = { Text("Teléfono") }
                )
                TextField(
                    value = updatedCompany.services,
                    onValueChange = { updatedCompany = updatedCompany.copy(services = it) },
                    label = { Text("Servicios") }
                )
                TextButton(
                    onClick = {
                        onDelete(company)
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Eliminar")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onUpdate(updatedCompany)
                onDismiss()
            }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}


@Composable
fun DropdownMenuFilter(classificationFilter: MutableState<String>) {
    val classifications = listOf("Consultoría", "Desarrollo a la medida", "Fábrica de software")
    val isDropDownExpanded = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                    text = if (classificationFilter.value.isEmpty()) "Filtrar por clasificación" else classificationFilter.value,
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
                classifications.forEach { classification ->
                    DropdownMenuItem(
                        onClick = {
                            classificationFilter.value = classification
                            isDropDownExpanded.value = false
                        },
                        text = { Text(text = classification) }
                    )
                }
            }
        }

        if (classificationFilter.value.isNotEmpty()) {
            TextButton(
                onClick = {
                    classificationFilter.value = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Quitar filtro", color = Color.White)
            }
        }
    }
}