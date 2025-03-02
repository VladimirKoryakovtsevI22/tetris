package com.example.tetris

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.tetris.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
    lateinit var tetrisGame: TetrisGame
    private lateinit var gameBoardView: GameBoardView
    private lateinit var gameBoard: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameBoard = binding.gameBoard
        gameBoardView = GameBoardView(gameBoard, this)

        tetrisGame = TetrisGame(gameBoardView, this)
        tetrisGame.setBoardUpdateListener(gameBoardView)
        tetrisGame.startGame()


        binding.linearLeft.setOnClickListener {
            tetrisGame.moveLeft()
        }

        binding.linearRight.setOnClickListener {
            tetrisGame.moveRight()
        }

        binding.linearRotate.setOnClickListener {
            tetrisGame.rotateTetromino()
        }

        binding.linearDown.setOnClickListener {
            tetrisGame.moveDown()
        }

    }

    fun showAlert(score: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Игра окончена")
        builder.setMessage("Ваш счёт: $score\nХотите начать заново?")
        builder.setPositiveButton("Да") { dialog, which ->
            startNewGame()
        }
        builder.setNegativeButton("Выход") { dialog, which ->
            finish()
        }
        builder.show()
    }

    private fun startNewGame() {
        tetrisGame.startGame()
    }


}