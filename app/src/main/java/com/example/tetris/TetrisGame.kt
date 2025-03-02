package com.example.tetris

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val lock = Any() // Объект для синхронизации

    fun setBoardUpdateListener(listener: BoardUpdateListener) {
        this.listener = listener
    }

    fun startCoord(shape: Array<IntArray>): Array<Cell> {
        val height = shape.size
        val width = shape[0].size

        val fieldCenterX = 10
        val startX = (fieldCenterX / 2) - (width / 2)  // Центр по оси X
        val startY = 0

        val blocks = ArrayList<Cell>()

        for (blockY in 0 until height) {
            for (blockX in 0 until width) {
                if (shape[blockY][blockX] == 1) {
                    blocks.add(Cell(startX + blockX, startY + blockY, isOccupied = true))
                }
            }
        }

        val blocksArray: Array<Cell> = blocks.toArray(arrayOf<Cell>())

        return blocksArray
    }

    fun newTetromino() {
        val shapeType = TETROMINOES.tetrominoType.random()
        val shape = TETROMINOES.getTetro(shapeType)
        val blocks = startCoord(shape)

        currentTetromino = Tetromino(shape = shape, isOccupied = true, blocks = blocks)

        if (checkInitialCollision(currentTetromino!!)) {
            gameOver = true
        } else {
            placeTetromino(currentTetromino!!)
            listener?.onBoardUpdated(board)
        }
    }

    fun placeTetromino(tetromino: Tetromino) {
        synchronized(lock) {
            for (i in tetromino.blocks.indices) {
                if (tetromino.blocks[i].isOccupied) {
                    val currX = tetromino.blocks[i].x
                    val currY = tetromino.blocks[i].y
                    if (currY in board.indices && currX in board[0].indices) {
                        board[currY][currX] = tetromino
                    }
                }
            }
        }
        Log.d("MyLog", "Функция placeTetromino устанавили тетромину")

    }

    fun checkInitialCollision(tetromino: Tetromino): Boolean {
        synchronized(lock) {
            for (i in tetromino.blocks.indices) {
                if (tetromino.blocks[i].isOccupied) {
                    val curX = tetromino.blocks[i].x
                    val curY = tetromino.blocks[i].y

                    if (curX !in 0 until BOARD_WIDTH || curY >= BOARD_HEIGHT) {
                        return true
                    }

                    if (board[curY][curX].isOccupied) {
                        return true
                    }
                }
            }
            return false
        }
    }

    fun checkCollision(tetromino: Tetromino): Boolean {
        synchronized(lock) {
            for (i in tetromino.blocks.indices) {
                if (tetromino.blocks[i].isOccupied) {
                    val curX = tetromino.blocks[i].x
                    val curY = tetromino.blocks[i].y + 1

                    // Проверка выхода за нижнюю границу
                    if (curY >= BOARD_HEIGHT) {
                        return true
                    }

                    // Проверка на столкновение с зафиксированными блоками
                    if (curY >= 0 && board[curY][curX] != currentTetromino && board[curY][curX].isOccupied) {
                        return true
                    }
                }
            }
            return false
        }
    }

    fun startGame(): Boolean {
        gameBoardView.initGameBoard()
        gameOver = false
        score = 0
        board = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { Tetromino() } }

        CoroutineScope(Dispatchers.Default).launch {
            while (!gameOver) {
                newTetromino()
                if (gameOver) break

                while (!checkCollision(currentTetromino!!)) {
                    listener?.onBoardUpdated(board)
                    moveDown()
                    clearFullLines()
                    delay(1000) // Неблокирующая задержка
                }
            }

            // Обновление UI в основном потоке
            withContext(Dispatchers.Main) {
                (context as? PlayActivity)?.showAlert(score)
            }
        }
        return true
    }

    fun rotateFigure(): Tetromino {
        synchronized(lock) {
            val figure = currentTetromino!!.blocks
            val centerX = figure.sumOf { it.x } / figure.size
            val centerY = figure.sumOf { it.y } / figure.size

            // Перезаписываем координаты каждой клетки
            figure.forEach { cell ->
                val newX = (cell.y - centerY) + centerX
                val newY = -(cell.x - centerX) + centerY
                cell.x = newX
                cell.y = newY
            }
            return Tetromino(currentTetromino!!.shape, isOccupied = true, blocks = figure)
        }
    }

    fun rotateTetromino() {
        synchronized(lock) {
            currentTetromino?.let { tetromino ->
                val rotated = rotateFigure()
                if (checkCollision(tetromino) && checkCollision(rotated)) {
                    removeTetromino(tetromino)
                    currentTetromino = rotated
                    placeTetromino(currentTetromino!!)
                    listener?.onBoardUpdated(board)
                }
            }
        }
    }

    fun quicklyMoveDown() {
        currentTetromino?.let { tetromino ->
            synchronized(lock) {
                // Продолжаем двигать фигуру вниз, пока это возможно
                while (!checkCollision(tetromino)) {
                    removeTetromino(tetromino)
                    for (cell in tetromino.blocks) {
                        if (cell.isOccupied) {
                            cell.y += 1
                        }
                    }
                    placeTetromino(tetromino)
                    listener?.onBoardUpdated(board)
                }

                placeTetromino(tetromino)
                clearFullLines()
                listener?.onBoardUpdated(board)
            }
        }
    }

    fun moveDown() {
        synchronized(lock) {
            currentTetromino?.let { tetromino ->
                Log.d("MyLog", "Функция moveDown")
                removeTetromino(tetromino)

                // Проверяем, можно ли двигаться дальше
                if (!checkCollision(tetromino)) {
                    for (cell in tetromino.blocks) {
                        if (cell.isOccupied) {
                            cell.y += 1
                        }
                    }
                } else {
                    placeTetromino(tetromino)
                    clearFullLines()
                    return
                }
                Log.d("MyLog", "Функция moveDown вызов placeTetromino")

                placeTetromino(tetromino)
                listener?.onBoardUpdated(board)
            }
        }
    }

    fun moveLeft() {
        synchronized(lock) {
            currentTetromino?.let {
                if (canMove(it, -1, 0)) {
                    removeTetromino(it)
                    for (cell in it.blocks) {
                        if (cell.isOccupied) {
                            cell.x -= 1
                        }
                    }

                    placeTetromino(it)
                    gameBoardView.onBoardUpdated(board)
                }
            }
        }
    }

    fun moveRight() {
        synchronized(lock) {
            currentTetromino?.let {
                if (canMove(it, 1, 0)) {
                    removeTetromino(it)
                    for (cell in it.blocks) {
                        if (cell.isOccupied) {
                            cell.x += 1
                        }
                    }

                    placeTetromino(it)
                    gameBoardView.onBoardUpdated(board)
                }
            }
        }
    }

    fun canMove(tetromino: Tetromino, dx: Int, dy: Int): Boolean {
        synchronized(lock) {
            for (i in tetromino.blocks.indices) {
                if (tetromino.blocks[i].isOccupied) {
                    var curX = tetromino.blocks[i].x + dx
                    var curY = tetromino.blocks[i].y + dy

                    // Проверяем выход за границы игрового поля
                    if (curX < 0 || curX >= BOARD_WIDTH || curY >= BOARD_HEIGHT) {
                        return false
                    }

                    // Проверяем столкновение с другими фигурами
                    if (board[curY][curX].isOccupied && board[curY][curX] != currentTetromino) {
                        return false
                    }
                }
            }
            return true
        }
    }

    fun removeTetromino(tetromino: Tetromino) {
        synchronized(lock) {
            for (i in tetromino.blocks.indices) {
                if (tetromino.blocks[i].isOccupied) {
                    var curX = tetromino.blocks[i].x
                    var curY = tetromino.blocks[i].y

                    if (curY in board.indices && curX in board[0].indices) {
                        board[curY][curX] = Tetromino()
                    }
                }
            }
            Log.d("MyLog", "удаляем tetromin")
        }
    }

    fun clearFullLines() {
        synchronized(lock) {
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
}

interface BoardUpdateListener {
    fun onBoardUpdated(board: Array<Array<Tetromino>>)
}