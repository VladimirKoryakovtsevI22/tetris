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
import androidx.lifecycle.lifecycleScope
import com.example.tetris.databinding.ActivityPlayBinding
import com.example.tetris.db.AppDatabase
import com.example.tetris.db.GameHistory
import com.example.tetris.db.GameHistoryDao
import kotlinx.coroutines.launch

class PlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
    lateinit var tetrisGame: TetrisGame
    private lateinit var gameBoardView: GameBoardView
    private lateinit var gameBoard: GridLayout
    private lateinit var gameHistoryDao: GameHistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameHistoryDao = AppDatabase.getDatabase(this).gameHistoryDao()
        gameBoard = binding.gameBoard
        gameBoardView = GameBoardView(gameBoard, this)

        tetrisGame = TetrisGame(gameBoardView, this)
        tetrisGame.setBoardUpdateListener(gameBoardView)
        tetrisGame.startGame()



        binding.cvBack.setOnClickListener {
            finish()
        }


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
            tetrisGame.quicklyMoveDown()
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
        saveGame(score)
        builder.show()
    }

    private fun saveGame(score: Int) {
        lifecycleScope.launch{
            val game = GameHistory(
                score = score,
                date = System.currentTimeMillis()
            )
            gameHistoryDao.insert(gameHistory = game)
        }
    }
    private fun startNewGame() {
        tetrisGame.startGame()
    }


}