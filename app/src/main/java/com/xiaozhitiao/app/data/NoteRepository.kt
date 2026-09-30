package com.xiaozhitiao.app.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val completedNotes: Flow<List<NoteEntity>> = noteDao.getCompletedNotes()
    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val notesWithReminders: Flow<List<NoteEntity>> = noteDao.getNotesWithReminders()
    val deletedNotes: Flow<List<NoteEntity>> = noteDao.getDeletedNotes()

    suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun softDeleteNotesByIds(noteIds: List<Int>) {
        noteDao.softDeleteNotesByIds(noteIds)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
    
    suspend fun getNoteById(noteId: Int): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }
    
    fun getUpcomingReminders(currentTime: Long): Flow<List<NoteEntity>> {
        return noteDao.getUpcomingReminders(currentTime)
    }
    
    // 获取所有带提醒的笔记
    fun getAllReminders(): Flow<List<NoteEntity>> {
        return noteDao.getNotesWithReminders()
    }
    
    // 设置提醒
    suspend fun setReminder(noteId: Int, reminderTime: Long, reminderMessage: String?): Boolean {
        val note = noteDao.getNoteById(noteId) ?: return false
        val updatedNote = note.copy(
            hasReminder = true,
            reminderTime = reminderTime,
            reminderMessage = reminderMessage
        )
        noteDao.updateNote(updatedNote)
        
        // 记录修改历史
        addEditHistory(
            noteId = noteId,
            editType = EDIT_TYPE_REMINDER_SET,
            oldValue = null,
            newValue = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(reminderTime))
        )
        
        return true
    }
    
    // 取消提醒
    suspend fun cancelReminder(noteId: Int): Boolean {
        val note = noteDao.getNoteById(noteId) ?: return false
        
        // 只有在有提醒的情况下才记录历史
        if (note.hasReminder) {
            val oldValue = note.reminderTime?.let {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(it))
            }
            
            // 记录修改历史
            addEditHistory(
                noteId = noteId,
                editType = EDIT_TYPE_REMINDER_CANCELED,
                oldValue = oldValue,
                newValue = null
            )
        }
        
        val updatedNote = note.copy(
            hasReminder = false,
            reminderTime = null,
            reminderMessage = null
        )
        noteDao.updateNote(updatedNote)
        
        return true
    }
    
    // 添加修改历史记录
    suspend fun addEditHistory(
        noteId: Int,
        editType: String,
        oldValue: String?,
        newValue: String?
    ): Long {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val history = EditHistoryEntity(
            noteId = noteId,
            editType = editType,
            oldValue = oldValue,
            newValue = newValue,
            editTime = now
        )
        return noteDao.insertEditHistory(history)
    }
    
    // 获取笔记的修改历史
    fun getEditHistoryForNote(noteId: Int): Flow<List<EditHistoryEntity>> {
        return noteDao.getEditHistoryForNote(noteId)
    }
    
    // 获取所有修改历史
    fun getAllEditHistory(): Flow<List<EditHistoryEntity>> {
        return noteDao.getAllEditHistory()
    }

    fun getDao(): NoteDao {
        return noteDao
    }
    
    suspend fun deleteNotesByIdsPermanently(noteIds: List<Int>) {
        noteDao.deleteNotesByIdsPermanently(noteIds)
    }
    
    suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }
    
    suspend fun restoreNoteById(noteId: Int) {
        noteDao.restoreNoteById(noteId)
    }
    
    companion object {
        const val EDIT_TYPE_REMINDER_SET = "REMINDER_SET"
        const val EDIT_TYPE_REMINDER_CANCELED = "REMINDER_CANCELED"
    }
} 