package com.example.tetris

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView

class GameBoardView(
    private val gameBoard: GridLayout,
    private val context: Context
) : BoardUpdateListener, View.OnTouchListener {

    private var startX = 0f
    private var startY = 0f
    private val moveThreshold = 50
    private val tapThreshold = 10f // Порог для распознавания тапа
    private val swipeDownThreshold = 150
    private val tapDelay = 300L // Задержка в миллисекундах для регистрации тапа
    private var lastTapTime = 0L // Время последнего тапа

    private var isMovingDown = false // Флаг для отслеживания, идет ли движение вниз

    init {
        gameBoard.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - startX
                val deltaY = event.y - startY

                if (deltaX > moveThreshold) {
                    // Свайп вправо
//                    (context as PlayActivity).tetrisGame.moveRight()
                    startX = event.x // Обновляем точку старта
                } else if (deltaX < -moveThreshold) {
                    // Свайп влево
//                    (context as PlayActivity).tetrisGame.moveLeft()
                    startX = event.x // Обновляем точку старта
                }

                if (deltaY > swipeDownThreshold && !isMovingDown) {
                    // Свайп вниз — перемещаем тетромино вниз быстро
                    isMovingDown = true
//                    (context as PlayActivity).tetrisGame.moveDown()
                    startY = event.y // Обновляем точку старта
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Определение тапа (если перемещение не превышает порог)
                val deltaX = event.x - startX
                val deltaY = event.y - startY
                val currentTime = System.currentTimeMillis()

                if (Math.abs(deltaX) < tapThreshold && Math.abs(deltaY) < tapThreshold) {
                    // Проверяем время между тапами, если прошло достаточно времени
                    if (currentTime - lastTapTime > tapDelay) {
                        lastTapTime = currentTime
                        // Если перемещение минимально, значит это был тап — вращаем тетромино
//                        (context as PlayActivity).tetrisGame.rotateTetromino()
                    }
                }

                // В случае завершения быстрого движения вниз, сбрасываем флаг
                if (deltaY > swipeDownThreshold) {
                    isMovingDown = false
                }

                return true
            }
        }
        return false
    }

    fun initGameBoard() {
        gameBoard.removeAllViews()

        for (y in 0 until TetrisGame.BOARD_HEIGHT) {
            for (x in 0 until TetrisGame.BOARD_WIDTH) {
                val cell = ImageView(context)
                cell.setImageResource(R.drawable.green_block)

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(x, 1f)
                    rowSpec = GridLayout.spec(y, 1f)
                }

                cell.layoutParams = params
                gameBoard.addView(cell)
            }
        }
    }

    override fun onBoardUpdated(board: Array<Array<Tetromino>>) {
        gameBoard.post {
            for (y in 0 until TetrisGame.BOARD_HEIGHT) {
                for (x in 0 until TetrisGame.BOARD_WIDTH) {
                    val index = y * TetrisGame.BOARD_WIDTH + x
                    val cell = gameBoard.getChildAt(index) as? ImageView

                    cell?.setImageResource(
                        if (board[y][x].isOccupied) R.drawable.lightblue_block
                        else R.drawable.green_block
                    )
                }
            }
        }
    }
}
