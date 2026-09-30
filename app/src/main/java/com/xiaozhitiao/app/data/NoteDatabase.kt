package com.xiaozhitiao.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        NoteEntity::class,
        EditHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        // 数据库迁移对象 - 从版本1迁移到版本2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加completedDate字段
                database.execSQL("ALTER TABLE notes ADD COLUMN completedDate TEXT")
            }
        }
        
        // 数据库迁移对象 - 从版本2迁移到版本3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建编辑历史记录表
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `edit_history` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`noteId` INTEGER NOT NULL, " +
                    "`editType` TEXT NOT NULL, " +
                    "`oldValue` TEXT, " +
                    "`newValue` TEXT, " +
                    "`editTime` TEXT NOT NULL, " +
                    "FOREIGN KEY(`noteId`) REFERENCES `notes`(`id`) ON DELETE CASCADE)"
                )
                
                // 创建外键索引以提高性能
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_edit_history_noteId` ON `edit_history` (`noteId`)")
            }
        }
        
        // 数据库迁移对象 - 从版本3迁移到版本4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加提醒相关的字段
                database.execSQL("ALTER TABLE notes ADD COLUMN hasReminder INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN reminderTime INTEGER")
                database.execSQL("ALTER TABLE notes ADD COLUMN reminderMessage TEXT")
            }
        }

        // 数据库迁移对象 - 从版本4迁移到版本5
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加软删除标记字段
                database.execSQL("ALTER TABLE notes ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // 数据库迁移对象 - 从版本5迁移到版本6，添加索引以提高查询性能
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 为completed和isDeleted列添加复合索引，优化getCompletedNotes和getActiveNotes查询
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_completed_isDeleted` ON `notes` (`completed`, `isDeleted`)")
                
                // 为completedDate添加索引，优化按completedDate排序的查询
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_completedDate` ON `notes` (`completedDate`)")
                
                // 为date添加索引，优化按date排序的查询
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_date` ON `notes` (`date`)")
                
                // 为hasReminder和reminderTime添加索引，优化提醒相关查询
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_hasReminder` ON `notes` (`hasReminder`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_reminderTime` ON `notes` (`reminderTime`)")
            }
        }
    }
} 