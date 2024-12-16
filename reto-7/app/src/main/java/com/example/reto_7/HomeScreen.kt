package com.example.reto_7

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tic Tac Toe",
            style = TextStyle(fontSize = 30.sp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { navController.navigate("game_screen") },
                ) {
                    Text(text = "Play Offline")
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { navController.navigate("game_screen") },
                ) {
                    Text(text = "Online Game")
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(color = Color.LightGray, modifier = Modifier.width(72.dp))
            Text(
                text = "or",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(color = Color.LightGray, modifier = Modifier.width(72.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it},
            label = { Text("Insert code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (code.text.isNotEmpty()) {
                    navController.navigate("game_screen")
                } else {
                    Toast.makeText(context, "Insert a valid code", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "Join by code", fontSize = 16.sp)
        }
    }
}
