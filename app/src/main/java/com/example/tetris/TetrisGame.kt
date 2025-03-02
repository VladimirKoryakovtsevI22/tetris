package com.example.tetris

import android.content.Context
import android.text.Selection.moveDown
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

    fun startCoord(shape: Array<IntArray>): Array<Array<Cell>> {
        val height = shape.size
        val width = shape[0].size

        val fieldCenterX = 10
        val startX = (fieldCenterX / 2) - (width / 2)  // Центр по оси X
        val startY = 0

        val blocks = Array(height) { Array(width) { Cell() } }

        for (blockY in 0 until height) {
            for (blockX in 0 until width) {
                if (shape[blockY][blockX] == 1) {
                    blocks[blockY][blockX] =
                        Cell(startX + blockX, startY + blockY, isOccupied = true)
                } else {
                    blocks[blockY][blockX] = Cell()
                }
            }
        }

        return blocks
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
        for (y in tetromino.blocks.indices) {
            for (x in tetromino.blocks[y].indices) {
                if (tetromino.blocks[y][x].isOccupied) {
                    val currX = tetromino.blocks[y][x].x
                    val currY = tetromino.blocks[y][x].y
                    if (currY in board.indices && currX in board[0].indices) {
                        board[currY][currX] = tetromino
                    }
                }
            }
        }
    }


    fun checkInitialCollision(tetromino: Tetromino): Boolean {
        // по x проверяет выходит ли за боковые границы
        // по y чекает не выходит ли за дно
        for (y in tetromino.blocks.indices) {
            for (x in tetromino.blocks[y].indices) {
                if (tetromino.blocks[y][x].isOccupied) {
                    val curX = tetromino.blocks[y][x].x
                    val curY = tetromino.blocks[y][x].y

                    if (curX !in 0 until BOARD_WIDTH || curY >= BOARD_HEIGHT) {
                        return true
                    }

                    if (board[curY][curX].isOccupied) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkCollision(tetromino: Tetromino): Boolean {
        for (y in tetromino.blocks.indices) {
            for (x in tetromino.blocks[y].indices) {
                if (tetromino.blocks[y][x].isOccupied) {
                    val curX = tetromino.blocks[y][x].x
                    val curY = tetromino.blocks[y][x].y + 1

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
        }
        return false
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

                while (!checkCollision(currentTetromino!!)) {
                    moveDown()
                    Thread.sleep(500)
//                    break

                }
            }

            // После завершения игры, вызываем showAlert в главном потоке
            (context as? PlayActivity)?.runOnUiThread {
                (context as? PlayActivity)?.showAlert(score)
            }
        }
        return true
    }

    fun moveDown() {
        currentTetromino?.let { tetromino ->
            removeTetromino(tetromino)

            // Проверяем, можно ли двигаться дальше
            if (!checkCollision(tetromino)) {
//                println("all good")
                for (row in tetromino.blocks) {
                    for (cell in row) {
                        if (cell.isOccupied) {
                            cell.y += 1
                        }
                    }
                }
            } else {
                placeTetromino(tetromino)
                clearFullLines()
                return
            }

            placeTetromino(tetromino)
            listener?.onBoardUpdated(board)
        }
    }

    fun removeTetromino(tetromino: Tetromino) {
        for (y in tetromino.blocks.indices) {
            for (x in tetromino.blocks[y].indices) {
                if (tetromino.blocks[y][x].isOccupied) {
                    val curX = tetromino.blocks[y][x].x
                    val curY = tetromino.blocks[y][x].y

                    if (curY in board.indices && curX in board[0].indices) {
                        board[curY][curX] = Tetromino()
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





//    fun canMove(tetromino: Tetromino, dx: Int, dy: Int): Boolean {
//        val shape = tetromino.shape ?: return false  // Если shape == null, движение невозможно
//
//        for (row in shape.indices) {
//            for (col in shape[row].indices) {
//                if (shape[row][col] == 1) {  // Проверяем только занятые клетки фигуры
//                    val newX = tetromino.x + col + dx
//                    val newY = tetromino.y + row + dy
//
//                    // Проверяем выход за границы игрового поля
//                    if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
//                        return false
//                    }
//
//                    // Проверяем столкновение с другими фигурами
//                    if (board[newY][newX].isOccupied && board[newY][newX] != currentTetromino) {
//                        return false
//                    }
//                }
//            }
//        }
//        return true
//    }


//        for (y in tetromino.shape!!.indices) {
//            for (x in tetromino.shape!![y].indices) {
//                if (tetromino.shape!![y][x] == 1) {
//                    val newX = tetromino.x + x
//                    val newY = tetromino.y + y
//
//                    // Проверка выхода за границы
//                    if (newX !in 0 until BOARD_WIDTH || newY >= BOARD_HEIGHT) {
//                        return true
//                    }
//
//                    // Проверка на занятость клетки (если newY >= 0, чтобы не проверять вне массива)
//                    if (newY >= 0 && board[newY][newX].isOccupied) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }

//    fun rotateTetromino() {
//        currentTetromino?.let { tetromino ->
//            val rotated = tetromino.rotate()
//            if (checkCollision(tetromino)) {
//                removeTetromino(tetromino)
//                currentTetromino = rotated
//                placeTetromino(rotated)
//                listener?.onBoardUpdated(board)
//            }
//        }
//    }

//    fun moveLeft() {
//        currentTetromino?.let {
//            if (canMove(it, -1, 0)) {
//                removeTetromino(it)
//                it.x -= 1
//                placeTetromino(it)
//                gameBoardView.onBoardUpdated(board)
//            }
//        }
////    }
//
//    fun moveRight() {
//        currentTetromino?.let {
//            if (canMove(it, 1, 0)) {
//                removeTetromino(it)
//                it.x += 1
//                placeTetromino(it)
//                gameBoardView.onBoardUpdated(board)
//            }
//        }
//    }


//







interface BoardUpdateListener {
    fun onBoardUpdated(board: Array<Array<Tetromino>>)
}

