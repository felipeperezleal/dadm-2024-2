package com.example.reto_7

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun GameActivity(
    navController: NavController
) {
    val context = LocalContext.current
    val soundPool = remember { createSoundPool(context) }
    val clickSoundId = remember { loadClickSound(context, soundPool) }
    val computerMoveSoundId = remember { loadComputerMoveSound(context, soundPool) }
    var isMuted by rememberSaveable  { mutableStateOf(false) }

    val backgroundColor = Color.White

    val gameLogic = remember { GameLogic() }
    var boardState by rememberSaveable  { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var gameMessage by rememberSaveable  { mutableStateOf("") }

    val (savedPlayerOneWins, savedPlayerTwoWins, savedTies) = loadScores(context)

    var playerOneWins by rememberSaveable  { mutableIntStateOf(savedPlayerOneWins) }
    var playerTwoWins by rememberSaveable  { mutableIntStateOf(savedPlayerTwoWins) }
    var ties by rememberSaveable  { mutableIntStateOf(savedTies) }

    var isGameOver by rememberSaveable  { mutableStateOf(false) }

    var selectedDifficulty by rememberSaveable { mutableStateOf(loadDifficulty(context)) }

    gameLogic.setDifficultyLevel(selectedDifficulty)

    gameLogic.onGameEnd = { message ->
        gameMessage = message
        if (message.contains("X wins") || message.contains("O wins") || message.contains("It's a tie")) {
            isGameOver = true
        }
        when {
            message.contains("X wins") -> playerOneWins++
            message.contains("O wins") -> playerTwoWins++
            message.contains("It's a tie") -> ties++
        }
        saveScores(context, playerOneWins, playerTwoWins, ties)
    }

    fun resetGame() {
        gameLogic.resetGame()
        boardState = Array(3) { Array(3) { "" } }
        gameMessage = ""
        isGameOver = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
            .statusBarsPadding()) {
        Screen(
            modifier = Modifier.align(Alignment.Center),
            boardState = boardState,
            gameMessage = gameMessage,
            onUserMove = { row, col ->
                if (!isGameOver) {
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
                }
            },
            onRefresh = { resetGame() },
            playerOneWins = playerOneWins,
            playerTwoWins = playerTwoWins,
            ties = ties,
            onDifficultyChange = { difficulty ->
                selectedDifficulty = difficulty
                gameLogic.setDifficultyLevel(selectedDifficulty)
                saveDifficulty(context, selectedDifficulty)
                resetGame()
            },
            selectedDifficulty = selectedDifficulty,
            onMuteToggle = { isMuted = !isMuted },
            isMuted = isMuted,
            soundPool = soundPool,
            clickSoundId = clickSoundId,
            computerMoveSoundId = computerMoveSoundId
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
    onDifficultyChange: (GameLogic.DifficultyLevel) -> Unit,
    selectedDifficulty: GameLogic.DifficultyLevel,
    onMuteToggle: () -> Unit,
    isMuted: Boolean,
    soundPool: SoundPool,
    clickSoundId: Int,
    computerMoveSoundId: Int
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
                text = "Difficulty: ${selectedDifficulty.name}",
                fontSize = 18.sp,
                color = Color(0xFF98c1d9),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Board(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                boardState = boardState,
                onUserMove = onUserMove,
                isMuted = isMuted,
                soundPool = soundPool,
                clickSoundId = clickSoundId,
                computerMoveSoundId = computerMoveSoundId
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
                onDifficultyChange = onDifficultyChange,
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
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Difficulty: ${selectedDifficulty.name}",
                    fontSize = 18.sp,
                    color = Color(0xFF98c1d9),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
                    onDifficultyChange = onDifficultyChange,
                    onMuteToggle = onMuteToggle,
                    isMuted = isMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Board(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                boardState = boardState,
                onUserMove = onUserMove,
                isMuted = isMuted,
                soundPool = soundPool,
                clickSoundId = clickSoundId,
                computerMoveSoundId = computerMoveSoundId
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
        Text(text = score.toString(), fontSize = 24.sp, color = Color(0xFF98c1d9),)
    }
}


@Composable
fun Board(modifier: Modifier, boardState: Array<Array<String>>, onUserMove: (Int, Int) -> Unit, isMuted: Boolean, soundPool: SoundPool, clickSoundId: Int, computerMoveSoundId: Int) {
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
                        },
                        isGameOver = true,
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
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        modifier = Modifier.size(100.dp),
    ) {
        when (button) {

            "X" -> {
                Image(painter = painterResource(id = R.drawable.ic_x), contentDescription = "X", modifier = Modifier.fillMaxSize())
                if (!isMuted) {
                    soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
                }
            }
            "O" -> {
                Image(painter = painterResource(id = R.drawable.ic_o), contentDescription = "O", modifier = Modifier.fillMaxSize())
                if (!isMuted) {
                    soundPool.play(computerMoveSoundId, 1f, 1f, 0, 0, 1f)
                }
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
    onDifficultyChange: (GameLogic.DifficultyLevel) -> Unit,
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
        DifficultySelectorButton(onDifficultyChange = onDifficultyChange)
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
fun DifficultySelectorButton(onDifficultyChange: (GameLogic.DifficultyLevel) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(GameLogic.DifficultyLevel.Expert) }
    val contextForToast = LocalContext.current.applicationContext

    fun onOptionSelected(difficulty: GameLogic.DifficultyLevel) {
        selectedOption = difficulty
        onDifficultyChange(difficulty)
        expanded = false
        Toast.makeText(contextForToast, "Selected difficulty: ${difficulty.name}", Toast.LENGTH_SHORT).show()

    }

    Box(
        modifier = Modifier
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        FooterButton(icon = Icons.Default.Settings) {
            expanded = true
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            GameLogic.DifficultyLevel.entries.forEach { difficulty ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(difficulty)
                    },
                    text = { Text(difficulty.name) }
                )
            }
        }
    }
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

fun saveDifficulty(context: Context, difficulty: GameLogic.DifficultyLevel) {
    val sharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("difficulty", difficulty.name)
    editor.apply()
}

fun loadDifficulty(context: Context): GameLogic.DifficultyLevel {
    val sharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    val difficultyName = sharedPreferences.getString("difficulty", GameLogic.DifficultyLevel.Expert.name)
    return GameLogic.DifficultyLevel.valueOf(difficultyName ?: GameLogic.DifficultyLevel.Expert.name)
}

fun saveScores(context: Context, playerOneWins: Int, playerTwoWins: Int, ties: Int) {
    val sharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("player_one_wins", playerOneWins)
    editor.putInt("player_two_wins", playerTwoWins)
    editor.putInt("ties", ties)
    editor.apply()
}

fun loadScores(context: Context): Triple<Int, Int, Int> {
    val sharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    val playerOneWins = sharedPreferences.getInt("player_one_wins", 0)
    val playerTwoWins = sharedPreferences.getInt("player_two_wins", 0)
    val ties = sharedPreferences.getInt("ties", 0)
    return Triple(playerOneWins, playerTwoWins, ties)
}
