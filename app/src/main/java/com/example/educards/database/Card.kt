package com.example.educards.database

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.educards.CardNotificationReceiver
import kotlin.jvm.java

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val answer: String,
    var rating: Int = 0,
    var eFactor: Double = 2.5,
    var nextReviewDate: Long = System.currentTimeMillis(),
    var currentInterval: Int = 0,
    val isBuiltIn: Boolean = false,
    var isArchived: Boolean = false
) {
    fun updateEFactor(q: Int) {
        val delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)
        eFactor = (eFactor + delta).coerceIn(1.3, 2.5)
    }
    fun updateIntervals(q: Int, context: Context) {
        val newInterval = when (q) {
            5 -> 1 * 24 * 60 * 60 * 1000
            4 -> 12 * 60 * 60 * 1000
            3 -> 8 * 60 * 60 * 1000
            2 -> 4 * 60 * 60 * 1000
            1 -> 2 * 60 * 60 * 1000
            0 -> 15 * 60 * 1000
            else -> currentInterval
        }
        currentInterval = newInterval
        nextReviewDate = System.currentTimeMillis() + newInterval

        scheduleNotification(context)
    }
    fun scheduleNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val intent = Intent(context, CardNotificationReceiver::class.java).apply {
            putExtra("card_id", id)
            putExtra("card_question", question)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextReviewDate,
            pendingIntent
        )
    }
    fun isDue(): Boolean {
        return System.currentTimeMillis() >= nextReviewDate
    }
}