package com.xiaozhitiao.app.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.xiaozhitiao.app.MainActivity
import com.xiaozhitiao.app.R
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

/**
 * 提醒广播接收器，接收系统闹钟广播并显示通知
 */
class ReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "提醒"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "您有一项待办事项"
        
        Log.d("ReminderReceiver", "收到提醒广播: 笔记ID=$noteId, 标题=$title, 时间=${System.currentTimeMillis()}")
        
        // 创建打开应用的Intent
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("note_id", noteId) // 可选：传递笔记ID以便应用打开时直接显示该笔记
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 创建通知渠道（Android 8.0及以上需要）
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "笔记提醒",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "笔记应用的提醒通知"
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                    setShowBadge(true)
                    setBypassDnd(true) // 绕过勿扰模式
                }
                notificationManager.createNotificationChannel(channel)
                Log.d("ReminderReceiver", "已创建或更新通知渠道")
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "创建通知渠道失败: ${e.message}")
            }
        }
        
        try {
            // 获取默认通知铃声
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            // 构建通知
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // 确保在drawable中有此图标
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri) // 为旧版Android设置声音
                .setVibrate(longArrayOf(0, 500, 200, 500)) // 为旧版Android设置振动
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 在锁屏上完全显示
                .build()
            
            // 显示通知
            notificationManager.notify(noteId, notification)
            Log.d("ReminderReceiver", "已显示通知: ID=$noteId")
            
        } catch (e: Exception) {
            Log.e("ReminderReceiver", "显示通知失败: ${e.message}")
            e.printStackTrace()
        }
    }
    
    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        private const val CHANNEL_ID = "reminder_channel"
    }
} 