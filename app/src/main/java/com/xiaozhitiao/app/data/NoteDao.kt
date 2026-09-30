package com.xiaozhitiao.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 LIMIT 1")
    suspend fun getFirstNote(): NoteEntity?

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE completed = 1 AND isDeleted = 0 AND completedDate IS NOT NULL ORDER BY completedDate DESC")
    fun getCompletedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE completed = 0 AND isDeleted = 0 ORDER BY date DESC")
    fun getActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId AND isDeleted = 0")
    suspend fun getNoteById(noteId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE hasReminder = 1 AND isDeleted = 0 ORDER BY reminderTime ASC")
    fun getNotesWithReminders(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE hasReminder = 1 AND isDeleted = 0 AND reminderTime > :currentTime ORDER BY reminderTime ASC")
    fun getUpcomingReminders(currentTime: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :noteId")
    suspend fun softDeleteNoteById(noteId: Int)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id IN (:ids)")
    suspend fun softDeleteNotesByIds(ids: List<Int>)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotesByIdsPermanently(ids: List<Int>)
    
    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("UPDATE notes SET isDeleted = 0 WHERE id = :noteId")
    suspend fun restoreNoteById(noteId: Int)

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY date DESC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Insert
    suspend fun insertEditHistory(history: EditHistoryEntity): Long

    @Query("SELECT * FROM edit_history WHERE noteId = :noteId ORDER BY editTime DESC")
    fun getEditHistoryForNote(noteId: Int): Flow<List<EditHistoryEntity>>

    @Query("SELECT * FROM edit_history ORDER BY editTime DESC")
    fun getAllEditHistory(): Flow<List<EditHistoryEntity>>
} 