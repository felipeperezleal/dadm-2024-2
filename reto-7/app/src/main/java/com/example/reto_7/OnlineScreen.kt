package com.example.reto_7

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun OnlineScreen(
    navController: NavController,
    gameId: String?
) {
    val context = LocalContext.current
    var gameState by remember { mutableStateOf<Map<String, Any>?>(null) }

    if (gameId != null) {
        Firebase.firestore.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    gameState = snapshot.data
                }
            }
    }

    if (gameState == null) {
        CircularProgressIndicator()
        return
    }

    val board = (gameState?.get("board") as List<String>)
    val currentPlayer = gameState?.get("turn") as String
    val playerOne = (gameState?.get("playerOne") as Map<String, String>)["name"]
    val playerTwo = (gameState?.get("playerTwo") as Map<String, String>?)?.get("name")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Tic Tac Toe", style = TextStyle(fontSize = 30.sp))

        Spacer(modifier = Modifier.height(20.dp))

        if (gameState?.get("status") == "active") {
            Text(text = "Turn: $currentPlayer", style = TextStyle(fontSize = 20.sp))
        }

        for (i in 0..2) {
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0..2) {
                    val index = i * 3 + j
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .clickable {
                                if (board[index] == "${index + 1}" && gameState?.get("turn") == currentPlayer) {
                                    if (gameId != null) {
                                        makeMove(gameId, index, currentPlayer)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[index],
                            style = TextStyle(fontSize = 30.sp, color = Color.White)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameState?.get("status") == "ended") {
            val winner = gameState?.get("winner") as String?
            Text(text = "Game Over! Winner: $winner", style = TextStyle(fontSize = 20.sp))
        }
    }
}

fun makeMove(gameId: String, index: Int, player: String) {
    val newBoard = Firebase.firestore.collection("games")
        .document(gameId)
        .get()
        .addOnSuccessListener { document ->
            val board = (document.data?.get("board") as List<String>).toMutableList()
            if (board[index] == "${index + 1}") {
                board[index] = player
                val nextTurn = if (player == "X") "O" else "X"

                Firebase.firestore.collection("games")
                    .document(gameId)
                    .update(
                        "board", board,
                        "turn", nextTurn
                    )
                    .addOnSuccessListener {
                        checkWinner(gameId, board)
                    }
            }
        }
}

fun checkWinner(gameId: String, board: List<String>) {
    val winPatterns = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    for (pattern in winPatterns) {
        if (board[pattern[0]] == board[pattern[1]] && board[pattern[1]] == board[pattern[2]]) {
            val winner = board[pattern[0]]
            Firebase.firestore.collection("games")
                .document(gameId)
                .update(
                    "status", "ended",
                    "winner", winner
                )
            return
        }
    }

    if (board.all { it == "X" || it == "O" }) {
        Firebase.firestore.collection("games")
            .document(gameId)
            .update(
                "status", "ended",
                "winner", "None"
            )
    }
}
