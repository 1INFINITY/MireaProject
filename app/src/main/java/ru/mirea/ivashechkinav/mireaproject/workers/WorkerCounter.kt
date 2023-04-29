package ru.mirea.ivashechkinav.mireaproject.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class WorkerCounter(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private var counter: Int

    init {
        counter = getCurrentCounter()
    }
    override fun doWork(): Result {
        Log.d(TAG, "doWork: start")
        try {
            TimeUnit.SECONDS.sleep(10)
            counter += 1
            writeMessage(applicationContext, counter)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Log.d(TAG, "doWork: end")
        return Result.success()
    }
    private fun getCurrentCounter(): Int {
        val sharedPreferences = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(WorkerCounter.WORKER_MSG, 0)
    }
    private fun writeMessage(context: Context, counter: Int) {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(WORKER_MSG, counter)
        editor.apply()
    }
    companion object {
        const val TAG = "UploadWorker"
        const val WORKER_MSG = "WorkerMessage"
    }
}