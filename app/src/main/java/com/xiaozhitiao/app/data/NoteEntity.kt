package com.xiaozhitiao.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val completed: Boolean = false,
    val backgroundColor: String = "default", // 默认背景色
    val completedDate: String? = null, // 添加完成时间字段
    val hasReminder: Boolean = false, // 是否设置了提醒
    val reminderTime: Long? = null, // 提醒的时间戳（毫秒）
    val reminderMessage: String? = null, // 提醒的消息内容
    val isDeleted: Boolean = false // 软删除标记
)

// 添加修改历史记录表
@Entity(
    tableName = "edit_history",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EditHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int, // 关联的笔记ID
    val editType: String, // 修改类型：DATE_CREATED, DATE_COMPLETED
    val oldValue: String?, // 旧值
    val newValue: String?, // 新值
    val editTime: String // 修改时间
) 