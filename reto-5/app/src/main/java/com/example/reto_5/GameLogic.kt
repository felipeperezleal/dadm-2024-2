package com.example.reto_5

import android.os.Handler
import android.os.Looper
import java.util.Random

class GameLogic {
    private val board = CharArray(9) { (it + 1).toString()[0] }
    private val boardSize = 9

    enum class DifficultyLevel {
        Easy, Harder, Expert
    }

    private var difficultyLevel: DifficultyLevel = DifficultyLevel.Expert
    private val random = Random()

    companion object {
        const val PLAYER_ONE = 'X'
        const val PLAYER_TWO = 'O'
    }

    var onGameEnd: ((String) -> Unit)? = null

    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    fun makeMove(position: Int, isHuman: Boolean) {
        if (position in 0 until boardSize && board[position] != PLAYER_ONE && board[position] != PLAYER_TWO) {
            board[position] = if (isHuman) PLAYER_ONE else PLAYER_TWO

            val result = checkForWinner()
            if (result != 0) {
                onGameEnd?.invoke(getResultMessage(result))
            } else if (isHuman) {
                getComputerMove()
            }
        }
    }

    private fun getComputerMove() {
        var move = -1

        when (difficultyLevel) {
            DifficultyLevel.Easy -> move = getRandomMove()
            DifficultyLevel.Harder -> {
                move = getWinningMove()
                if (move == -1) move = getRandomMove()
            }
            DifficultyLevel.Expert -> {
                move = getWinningMove()
                if (move == -1) move = getBlockingMove()
                if (move == -1) move = getRandomMove()
            }
        }
        if (move != -1) {
            board[move] = PLAYER_TWO
            onGameEnd?.invoke("Player two is moving to ${move + 1}")
            val result = checkForWinner()
            if (result != 0) {
                onGameEnd?.invoke(getResultMessage(result))
            }
        }
    }

    private fun getRandomMove(): Int {
        var move: Int
        do {
            move = random.nextInt(boardSize)
        } while (board[move] == PLAYER_ONE || board[move] == PLAYER_TWO)
        return move
    }

    private fun getWinningMove(): Int {
        for (i in 0 until boardSize) {
            if (board[i] != PLAYER_ONE && board[i] != PLAYER_TWO) {
                val current = board[i]
                board[i] = PLAYER_TWO
                if (checkForWinner() == 3) {
                    board[i] = current
                    return i
                } else {
                    board[i] = current
                }
            }
        }
        return -1
    }

    private fun getBlockingMove(): Int {
        for (i in 0 until boardSize) {
            if (board[i] != PLAYER_ONE && board[i] != PLAYER_TWO) {
                val current = board[i]
                board[i] = PLAYER_ONE
                if (checkForWinner() == 2) {
                    board[i] = PLAYER_TWO
                    return i
                } else {
                    board[i] = current
                }
            }
        }
        return -1
    }

    private fun checkForWinner(): Int {
        for (i in 0..6 step 3) {
            if (board[i] == PLAYER_ONE && board[i + 1] == PLAYER_ONE && board[i + 2] == PLAYER_ONE) return 2
            if (board[i] == PLAYER_TWO && board[i + 1] == PLAYER_TWO && board[i + 2] == PLAYER_TWO) return 3
        }

        for (i in 0..2) {
            if (board[i] == PLAYER_ONE && board[i + 3] == PLAYER_ONE && board[i + 6] == PLAYER_ONE) return 2
            if (board[i] == PLAYER_TWO && board[i + 3] == PLAYER_TWO && board[i + 6] == PLAYER_TWO) return 3
        }

        if ((board[0] == PLAYER_ONE && board[4] == PLAYER_ONE && board[8] == PLAYER_ONE) ||
            (board[2] == PLAYER_ONE && board[4] == PLAYER_ONE && board[6] == PLAYER_ONE)) return 2
        if ((board[0] == PLAYER_TWO && board[4] == PLAYER_TWO && board[8] == PLAYER_TWO) ||
            (board[2] == PLAYER_TWO && board[4] == PLAYER_TWO && board[6] == PLAYER_TWO)) return 3

        for (i in 0 until boardSize) {
            if (board[i] != PLAYER_ONE && board[i] != PLAYER_TWO) return 0
        }

        return 1
    }

    private fun getResultMessage(result: Int): String {
        return when (result) {
            1 -> "It's a tie"
            2 -> "$PLAYER_ONE wins!"
            3 -> "$PLAYER_TWO wins!"
            else -> "There is a logic problem!"
        }
    }

    fun resetGame() {
        for (i in board.indices) {
            board[i] = (i + 1).toString()[0]
        }
    }

    fun getBoardState(): CharArray {
        return board.clone()
    }

}
