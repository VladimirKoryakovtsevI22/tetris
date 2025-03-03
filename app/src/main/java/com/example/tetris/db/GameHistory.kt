package com.example.tetris.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "game_history")
data class GameHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val date: Long
)