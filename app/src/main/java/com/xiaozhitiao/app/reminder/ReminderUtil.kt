package com.xiaozhitiao.app.reminder

import android.content.Context
import android.util.Log
import com.xiaozhitiao.app.data.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 提醒工具类，提供提醒相关的辅助功能
 */
object ReminderUtil {

    /**
     * 检查是否有错过的提醒
     * 
     * @param notes 笔记列表
     * @param context 上下文
     * @return 错过的提醒列表
     */
    fun checkMissedReminders(notes: List<NoteEntity>, context: Context): List<NoteEntity> {
        val currentTime = System.currentTimeMillis()
        val missedReminders = notes.filter { note ->
            note.hasReminder && note.reminderTime != null && note.reminderTime < currentTime
        }
        
        if (missedReminders.isNotEmpty()) {
            Log.d("ReminderUtil", "发现${missedReminders.size}个错过的提醒")
            
            // 显示错过的提醒
            val reminderManager = ReminderManager(context)
            missedReminders.forEach { note ->
                val title = note.title.ifEmpty { "提醒" }
                val message = note.reminderMessage ?: note.content.ifEmpty { "您有一项待办事项" }
                
                // 创建错过提醒的通知
                val reminderReceiver = ReminderReceiver()
                val intent = android.content.Intent().apply {
                    putExtra(ReminderReceiver.EXTRA_NOTE_ID, note.id)
                    putExtra(ReminderReceiver.EXTRA_TITLE, "错过的提醒: $title")
                    putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
                }
                reminderReceiver.onReceive(context, intent)
                
                Log.d("ReminderUtil", "显示错过的提醒: 笔记ID=${note.id}, 标题=$title, 提醒时间=${formatTime(note.reminderTime!!)}")
            }
        }
        
        return missedReminders
    }
    
    /**
     * 格式化时间
     */
    private fun formatTime(timeInMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timeInMillis))
    }
} 