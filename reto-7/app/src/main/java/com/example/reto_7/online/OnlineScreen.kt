package com.example.reto_7.online

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reto_7.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun OnlineScreen(
    navController: NavController,
    gameId: String?
) {
    val context = LocalContext.current
    val backgroundColor = Color.White

    var gameState by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    val soundPool = remember { createSoundPool(context) }
    val clickSoundId = remember { loadClickSound(context, soundPool) }
    val computerMoveSoundId = remember { loadComputerMoveSound(context, soundPool) }

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

    val gameMessage = if (gameState?.get("status") == "active") {
        "Turn: $currentPlayer"
    } else {
        val winner = gameState?.get("winner") as String?
        if (winner == "None") {
            "Game Over! It's a tie!"
        } else {
            "Game Over! Winner: $winner"
        }
    }

    val playerOneWins = 0
    val playerTwoWins = 0
    val ties = 0

    val onUserMove: (Int, Int) -> Unit = { row, col ->
        if (gameId != null) {
            makeMove(gameId, row * 3 + col, currentPlayer)
        }
    }

    val onRefresh: () -> Unit = {
    }

    val onMuteToggle: () -> Unit = {
        isMuted = !isMuted
    }

    val boardState = Array(3) { row ->
        Array(3) { col ->
            board[row * 3 + col]
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
            .statusBarsPadding()) {
        Screen(
            modifier = Modifier.fillMaxSize(),
            boardState = boardState,
            gameMessage = gameMessage,
            onUserMove = onUserMove,
            onRefresh = onRefresh,
            playerOneWins = playerOneWins,
            playerTwoWins = playerTwoWins,
            ties = ties,
            onMuteToggle = onMuteToggle,
            isMuted = isMuted,
            soundPool = soundPool,
            clickSoundId = clickSoundId,
            computerMoveSoundId = computerMoveSoundId,
            gameId = gameId
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
    playerOneWins: Int,
    playerTwoWins: Int,
    ties: Int,
    onMuteToggle: () -> Unit,
    isMuted: Boolean,
    soundPool: SoundPool,
    clickSoundId: Int,
    computerMoveSoundId: Int,
    gameId: String?
) {
    val config = LocalConfiguration.current
    val isPortrait = config.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(modifier = modifier) {
            Header()
            Spacer(modifier = Modifier.padding(16.dp))
            Scoreboard(
                playerOneWins = playerOneWins,
                playerTwoWins = playerTwoWins,
                ties = ties
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "Game ID: $gameId",
                fontSize = 18.sp,
                color = Color(0xFF98c1d9),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Board(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                onUserMove = onUserMove,
                isMuted = isMuted,
                soundPool = soundPool,
                clickSoundId = clickSoundId,
                computerMoveSoundId = computerMoveSoundId,
                isGameOver = gameMessage.contains("Game Over"),
                boardState = boardState
                )
            Text(
                text = gameMessage,
                color = Color(0xFF98c1d9),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(0.2f))
            Footer(
                onRefresh = onRefresh,
                onMuteToggle = onMuteToggle,
                isMuted = isMuted
            )
            Spacer(modifier = Modifier.weight(0.2f))
        }
    } else {
        Row(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
                    .weight(0.7f)
            ) {
                Header()
                Spacer(modifier = Modifier.padding(8.dp))
                Scoreboard(
                    playerOneWins = playerOneWins,
                    playerTwoWins = playerTwoWins,
                    ties = ties
                )
                Spacer(modifier = Modifier.weight(0.2f))
                Text(
                    text = gameMessage,
                    color = Color(0xFF98c1d9),
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.weight(0.2f))
                Footer(
                    onRefresh = onRefresh,
                    onMuteToggle = onMuteToggle,
                    isMuted = isMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Board(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onUserMove = onUserMove,
                isMuted = isMuted,
                soundPool = soundPool,
                clickSoundId = clickSoundId,
                boardState = boardState,
                computerMoveSoundId = computerMoveSoundId,
                isGameOver = gameMessage.contains("Game Over")
            )
        }
    }
}

@Composable
fun Header() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text("Tic Tac Toe", fontSize = 24.sp)
    }
}

@Composable
fun Scoreboard(
    playerOneWins: Int,
    playerTwoWins: Int,
    ties: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ScoreCard(title = "Player X", score = playerOneWins)
        ScoreCard(title = "Player O", score = playerTwoWins)
        ScoreCard(title = "Ties", score = ties)
    }
}

@Composable
fun ScoreCard(title: String, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 16.sp)
        Text(text = score.toString(), fontSize = 24.sp, color = Color(0xFF98c1d9))
    }
}

@Composable
fun Board(
    modifier: Modifier,
    onUserMove: (Int, Int) -> Unit,
    isMuted: Boolean,
    soundPool: SoundPool,
    clickSoundId: Int,
    computerMoveSoundId: Int,
    boardState: Array<Array<String>>,
    isGameOver: Boolean
) {
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
                    val buttonText = boardState[row][col]
                    TicTacToeButton(
                        button = buttonText,
                        onClick = {
//                            if (!isGameOver && buttonText == "") {
                                onUserMove(row, col)
//                            }
                        },
                        isGameOver = isGameOver,
                        isMuted = isMuted,
                        soundPool = soundPool,
                        clickSoundId = clickSoundId,
                        computerMoveSoundId = computerMoveSoundId
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
    isGameOver: Boolean,
    isMuted: Boolean,
    soundPool: SoundPool,
    clickSoundId: Int,
    computerMoveSoundId: Int
) {
    Button(
        onClick = {
//            if (!isGameOver && button == "") {
                onClick()
                if (!isMuted) {
                    soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
                }
//            }
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        modifier = Modifier.size(100.dp),
    ) {
        when (button) {
            "X" -> {
                Image(painter = painterResource(id = R.drawable.ic_x), contentDescription = "X", modifier = Modifier.fillMaxSize())
            }
            "O" -> {
                Image(painter = painterResource(id = R.drawable.ic_o), contentDescription = "O", modifier = Modifier.fillMaxSize())
            }
            else -> Unit
        }
    }
}

fun createSoundPool(context: Context): SoundPool {
    return SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
}

fun loadClickSound(context: Context, soundPool: SoundPool): Int {
    return soundPool.load(context, R.raw.tap, 1)
}

fun loadComputerMoveSound(context: Context, soundPool: SoundPool): Int {
    return soundPool.load(context, R.raw.tap2, 1)
}

@Composable
fun Footer(
    onRefresh: () -> Unit,
    onMuteToggle: () -> Unit,
    isMuted: Boolean
) {
    val contextForExit = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        FooterButton(Icons.Default.Close) {
            showExitDialog = true
        }
        FooterButton(Icons.Default.Refresh) { onRefresh() }
        MuteButton(onMuteToggle = onMuteToggle, isMuted = isMuted)
    }
    if (showExitDialog) {
        ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirmExit = {
                (contextForExit as? Activity)?.finish()
            }
        )
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
        Icon(imageVector = icon,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Are you sure you want to quit?")
        },
        confirmButton = {
            TextButton(onClick = onConfirmExit) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun MuteButton(onMuteToggle: () -> Unit, isMuted: Boolean) {
    Button(
        onClick = onMuteToggle,
        modifier = Modifier.size(64.dp)
    ) {
        val iconRes = if (isMuted) {
            R.drawable.volume_off
        } else {
            R.drawable.volume_up
        }

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = if (isMuted) "Muted" else "Unmuted",
            modifier = Modifier.size(32.dp)
        )
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

fun saveScores(context: Context, playerOneWins: Int, playerTwoWins: Int, ties: Int) {
    val sharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("player_one_wins", playerOneWins)
    editor.putInt("player_two_wins", playerTwoWins)
    editor.putInt("ties", ties)
    editor.apply()
}