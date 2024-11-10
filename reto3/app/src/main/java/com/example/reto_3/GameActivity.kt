package com.example.reto_3

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun  GameActivity() {
    val backgroundColor = Color.White

    val gameLogic = remember { GameLogic() }
    var boardState by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var gameMessage by remember { mutableStateOf("") }

    gameLogic.onGameEnd = { message ->
        gameMessage = message
    }

    fun resetGame() {
        gameLogic.resetGame()
        boardState = Array(3) { Array(3) { "" } }
        gameMessage = ""
    }

    Box(Modifier.fillMaxSize().padding(16.dp).background(backgroundColor)) {
        Screen(
            modifier = Modifier.align(Alignment.Center),
            boardState = boardState,
            gameMessage = gameMessage,
            onUserMove = { row, col ->
                val position = row * 3 + col
                gameLogic.makeMove(position, true)
                boardState[row][col] = GameLogic.PLAYER_ONE.toString()
                gameLogic.makeMove(-1, false)
                val currentBoardState = gameLogic.getBoardState()
                for (i in 0..2) {
                    for (j in 0..2) {
                        boardState[i][j] = when (currentBoardState[i * 3 + j]) {
                            GameLogic.PLAYER_ONE -> "X"
                            GameLogic.PLAYER_TWO -> "O"
                            else -> ""
                        }
                    }
                }
            },
            onRefresh = { resetGame() },
        )
    }
}

@Composable
fun Screen(
    modifier: Modifier,
    boardState: Array<Array<String>>,
    gameMessage: String,
    onUserMove: (Int, Int) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(modifier = modifier) {
        Header()
        Spacer(modifier = Modifier.padding(16.dp))
        Board(modifier = Modifier.weight(1f), boardState = boardState, onUserMove = onUserMove)
            Text(text = gameMessage, color = Color.Red, fontSize = 20.sp, modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.padding(16.dp))
        Footer(onRefresh = onRefresh)
    }
}

@Composable
fun Header() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text("Tic Tac Toe", fontSize = 24.sp)
    }
}
@Composable
fun Board(modifier: Modifier, boardState: Array<Array<String>>, onUserMove: (Int, Int) -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0..2) {
                    TicTacToeButton(
                        button = boardState[row][col],
                        onClick = {
                            onUserMove(row, col)
                        }
                    )
                    if (col < 2) {
                        VerticalDivider(color = Color.Gray, modifier = Modifier.height(100.dp))
                    }
                }
            }
            if (row < 2) {
                HorizontalDivider(color = Color.Gray, modifier = Modifier.width(300.dp))
            }
        }
    }
}

@Composable
fun TicTacToeButton(
    button: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        modifier = Modifier.size(100.dp),
    ) {
        Text(
            text = button,
            fontSize = 50.sp
        )
    }
}

@Composable
fun Footer(onRefresh: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        FooterButton(Icons.Default.Refresh) { onRefresh() }
        FooterButton(Icons.Default.Home) {  }
        FooterButton(Icons.Default.Settings) {  }
    }
}

@Composable
fun FooterButton(icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
    }
}
