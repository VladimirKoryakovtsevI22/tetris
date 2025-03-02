package com.example.tetris

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kotlin.concurrent.thread


class TetrisGame(
    private val gameBoardView: GameBoardView,
    private val context: Context
) {

    companion object {
        const val BOARD_WIDTH = 10
        const val BOARD_HEIGHT = 20
    }

    var board = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { Tetromino() } }

    var currentTetromino: Tetromino? = null
    var gameOver = false
    var score = 0

    var listener: BoardUpdateListener? = null

    fun setBoardUpdateListener(listener: BoardUpdateListener) {
        this.listener = listener
    }

    fun newTetromino() {
        val shapeType = TETROMINOES.tetrominoType.random()
        val shape = TETROMINOES.getTetro(shapeType)
        currentTetromino = Tetromino(shape, x = BOARD_WIDTH / 2 - 1, y = 0, isOccupied = true)

        if (checkInitialCollision(currentTetromino!!)) {
            // если нельзя заспаунить
            gameOver = true
        } else {
            placeTetromino(currentTetromino!!)
            listener?.onBoardUpdated(board) // Уведомляем о новом тетромино
        }
    }


    fun startGame(): Boolean {
        gameBoardView.initGameBoard()
        gameOver = false
        score = 0
        board = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { Tetromino() } }

        thread {
            while (!gameOver) {
                newTetromino()
                if (gameOver) break

                // Пока фигура может двигаться вниз — двигаем её
                while (!checkCollision(currentTetromino!!)) {
                    moveDown()
                    Thread.sleep(500)
                }
            }

            // После завершения игры, вызываем showAlert в главном потоке
            (context as? PlayActivity)?.runOnUiThread {
                (context as? PlayActivity)?.showAlert(score)
            }
        }
        return true
    }


    fun checkInitialCollision(tetromino: Tetromino): Boolean {
        // по x проверяет выходит ли за боковые границы
        // по y чекает не выходит ли за дно
        for (y in tetromino.shape!!.indices) {
            for (x in tetromino.shape!![y].indices) {
                if (tetromino.shape!![y][x] == 1) {
                    val newX = tetromino.x + x
                    val newY = tetromino.y + y

                    // Проверка выхода за границы
                    if (newX !in 0 until BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                        return true
                    }

                    // Проверка на занятость клетки (если newY >= 0, чтобы не проверять вне массива)
                    if (newY >= 0 && board[newY][newX].isOccupied) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun moveDown() {
        currentTetromino?.let { tetromino ->
            removeTetromino(tetromino)  // Убираем текущую фигуру с доски

            // Проверяем, можно ли двигаться дальше
            if (!checkCollision(tetromino)) {
                tetromino.y += 1  // Если нет столкновения, двигаем фигуру вниз
            } else {
                placeTetromino(tetromino)
                clearFullLines()
                return
            }

            placeTetromino(tetromino)
            listener?.onBoardUpdated(board)
        }
    }


    fun checkCollision(tetromino: Tetromino): Boolean {
        for (y in tetromino.shape!!.indices) {
            for (x in tetromino.shape!![y].indices) {
                if (tetromino.shape!![y][x] == 1) {
                    val newX = tetromino.x + x
                    val newY = tetromino.y + y + 1

                    // Проверка выхода за нижнюю границу
                    if (newY >= BOARD_HEIGHT) {
                        return true
                    }

                    // Проверка на столкновение с зафиксированными блоками
                    if (newY >= 0 && board[newY][newX] != currentTetromino && board[newY][newX].isOccupied) {
                        return true
                    }
                }
            }
        }
        return false
    }


    fun removeTetromino(tetromino: Tetromino) {
        for (y in tetromino.shape!!.indices) {
            for (x in tetromino.shape!![y].indices) {
                if (tetromino.shape!![y][x] == 1) {
                    val posX = tetromino.x + x
                    val posY = tetromino.y + y
                    if (posY in board.indices && posX in board[0].indices) {
                        board[posY][posX] = Tetromino()
                    }
                }
            }
        }
    }


    fun placeTetromino(tetromino: Tetromino) {
        for (y in tetromino.shape!!.indices) {
            for (x in tetromino.shape!![y].indices) {
                if (tetromino.shape!![y][x] == 1) {
                    val posX = tetromino.x + x
                    val posY = tetromino.y + y
                    if (posY in board.indices && posX in board[0].indices) {
                        board[posY][posX] = tetromino
                    }
                }
            }
        }
    }

    fun clearFullLines() {
        val remainingRows = board.filterNot { row -> row.all { it.isOccupied } }
        val numCleared = BOARD_HEIGHT - remainingRows.size

        if (numCleared > 0) {
            score += numCleared * 100
            val emptyRows = Array(numCleared) { Array(BOARD_WIDTH) { Tetromino() } }
            board = emptyRows + remainingRows.toTypedArray()
            listener?.onBoardUpdated(board)
        }
    }

}

interface BoardUpdateListener {
    fun onBoardUpdated(board: Array<Array<Tetromino>>)
}

