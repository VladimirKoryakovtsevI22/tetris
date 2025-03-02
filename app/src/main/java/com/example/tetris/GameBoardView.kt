package com.example.tetris

import android.content.Context
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class GameBoardView(private val gameBoard: GridLayout, private val context: Context) :
    BoardUpdateListener {

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
