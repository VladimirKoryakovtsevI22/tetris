package com.example.tetris.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {

    @Insert
    suspend fun insert(gameHistory: GameHistory)

    @Query("SELECT * FROM game_history ORDER BY date DESC")
    suspend fun getAll(): List<GameHistory>

    @Query("DELETE FROM game_history")
    suspend fun deleteAll()
}