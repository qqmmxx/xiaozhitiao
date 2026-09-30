package com.xiaozhitiao.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.provider.Settings
import android.app.Activity
import android.net.Uri
import android.app.AlarmManager.AlarmClockInfo
import com.xiaozhitiao.app.MainActivity

/**
 * 提醒管理器，负责设置和取消系统提醒
 */
class ReminderManager(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * 设置提醒
     * 
     * @param noteId 笔记ID
     * @param timeInMillis 提醒时间（毫秒）
     * @param title 提醒标题
     * @param message 提醒内容
     */
    fun scheduleReminder(noteId: Int, timeInMillis: Long, title: String, message: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_NOTE_ID, noteId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId, // 使用笔记ID作为请求码，确保每个笔记的提醒是唯一的
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            // 使用setAlarmClock代替setExactAndAllowWhileIdle，这样可以更可靠地触发提醒
            // 并且在锁屏界面显示提醒图标
            val showIntent = Intent(context, MainActivity::class.java)
            val showPendingIntent = PendingIntent.getActivity(
                context, 
                noteId, 
                showIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val alarmClockInfo = AlarmClockInfo(timeInMillis, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            
            Log.d("ReminderManager", "已设置闹钟提醒: 笔记ID=$noteId, 时间=$timeInMillis, 当前时间=${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("ReminderManager", "设置提醒失败: ${e.message}")
            
            // 如果设置闹钟失败，尝试使用setExactAndAllowWhileIdle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Log.d("ReminderManager", "已设置精确提醒(备用方案): 笔记ID=$noteId, 时间=$timeInMillis")
                } else {
                    Log.e("ReminderManager", "无法设置精确闹钟，请检查权限")
                    // 在实际应用中，应该提示用户授予SCHEDULE_EXACT_ALARM权限
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("ReminderManager", "已设置提醒(备用方案): 笔记ID=$noteId, 时间=$timeInMillis")
            }
        }
    }
    
    /**
     * 取消提醒
     * 
     * @param noteId 笔记ID
     */
    fun cancelReminder(noteId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        
        Log.d("ReminderManager", "已取消提醒: 笔记ID=$noteId")
    }
    
    /**
     * 检查是否有权限设置精确闹钟
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Android 12以下版本不需要特殊权限
        }
    }
    
    /**
     * 请求精确闹钟权限
     */
    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
        }
    }
} 