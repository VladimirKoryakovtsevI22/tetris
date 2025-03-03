package com.example.tetris

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tetris.db.GameHistory
import java.text.SimpleDateFormat
import java.util.*

class GameHistoryAdapter :
    ListAdapter<GameHistory, GameHistoryAdapter.GameHistoryViewHolder>(GameHistoryComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_history, parent, false)
        return GameHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameHistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class GameHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scoreView: TextView = itemView.findViewById(R.id.score)
        private val dateView: TextView = itemView.findViewById(R.id.date)

        fun bind(gameHistory: GameHistory) {
            scoreView.text = "Опыт: " + gameHistory.score.toString()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateView.text = "Дата: " + dateFormat.format(Date(gameHistory.date))
        }
    }

    class GameHistoryComparator : DiffUtil.ItemCallback<GameHistory>() {
        override fun areItemsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem == newItem
        }
    }
}