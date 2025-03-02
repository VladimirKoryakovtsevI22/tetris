package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class TetrisView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback, Runnable {
    private var thread: Thread? = null
    private var running = false
    private val paint = Paint()

    init {
        holder.addCallback(this)
        paint.color = Color.BLUE
        paint.style = Paint.Style.FILL
    }

    override fun run() {
        while (running) {
            if (holder.surface.isValid) {
                val canvas: Canvas = holder.lockCanvas()
                canvas.drawColor(Color.BLACK)
                canvas.drawRect(100f, 100f, 200f, 200f, paint) // Фигура
                holder.unlockCanvasAndPost(canvas)
            }
            Thread.sleep(1000 / 30)
        }
    }

    fun startGame() {
        running = true
        thread = Thread(this)
        thread?.start()
    }

    fun stopGame() {
        running = false
        thread?.join()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startGame()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
}
