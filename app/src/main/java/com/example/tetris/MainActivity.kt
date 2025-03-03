package com.example.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.db.AppDatabase
import com.example.tetris.db.GameHistoryDao
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameHistoryAdapter
    private lateinit var gameHistoryDao: GameHistoryDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvHistory
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GameHistoryAdapter()
        recyclerView.adapter = adapter

        gameHistoryDao = AppDatabase.getDatabase(this).gameHistoryDao()
        reloadDataHistory()


        binding.cvPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        reloadDataHistory()
    }

    private fun reloadDataHistory() {
        lifecycleScope.launch {
            val sessions = gameHistoryDao.getAll()
            adapter.submitList(sessions)
        }
    }
}