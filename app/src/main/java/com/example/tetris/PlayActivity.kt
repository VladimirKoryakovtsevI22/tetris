package com.example.tetris

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.tetris.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
    private lateinit var tetrisGame: TetrisGame
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

    }

    fun showAlert(score: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Игра окончена")
        builder.setMessage("Ваш счёт: $score\nХотите начать заново?")
        builder.setPositiveButton("Да") { dialog, which ->
            startNewGame() // Перезапуск игры
        }
        builder.setNegativeButton("Выход") { dialog, which ->
            finish() // Закрыть активность
        }
        builder.show() // Показываем диалог
    }

    private fun startNewGame() {
        tetrisGame.startGame()
    }



}