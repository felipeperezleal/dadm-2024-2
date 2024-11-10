package com.example.reto_3

import java.util.Random

class GameLogic {
    private val board = CharArray(9) { (it + 1).toString()[0] }
    private val boardSize = 9

    companion object {
        const val PLAYER_ONE = 'X'
        const val PLAYER_TWO = 'O'
    }

    private val random = Random()

    var onGameEnd: ((String) -> Unit)? = null

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
        for (i in 0 until boardSize) {
            if (board[i] != PLAYER_ONE && board[i] != PLAYER_TWO) {
                val current = board[i]
                board[i] = PLAYER_TWO
                if (checkForWinner() == 3) {
                    onGameEnd?.invoke("$PLAYER_TWO wins!")
                    return
                } else {
                    board[i] = current
                }
            }
        }

        for (i in 0 until boardSize) {
            if (board[i] != PLAYER_ONE && board[i] != PLAYER_TWO) {
                val current = board[i]
                board[i] = PLAYER_ONE
                if (checkForWinner() == 2) {
                    board[i] = PLAYER_TWO
                    onGameEnd?.invoke("Computer is moving to ${i + 1}")
                    return
                } else {
                    board[i] = current
                }
            }
        }

        var move: Int
        do {
            move = random.nextInt(boardSize)
        } while (board[move] == PLAYER_ONE || board[move] == PLAYER_TWO)

        board[move] = PLAYER_TWO
        onGameEnd?.invoke("Computer is moving to ${move + 1}")
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
