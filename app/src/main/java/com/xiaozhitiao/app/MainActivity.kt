package com.xiaozhitiao.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.xiaozhitiao.app.data.NoteDatabase
import com.xiaozhitiao.app.data.NoteEntity
import com.xiaozhitiao.app.data.NoteRepository
import com.xiaozhitiao.app.data.EditHistoryEntity
import com.xiaozhitiao.app.ui.theme.MyApplicationTheme
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Sort
import java.util.Calendar
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.xiaozhitiao.app.reminder.ReminderDateTimePickerDialog
import com.xiaozhitiao.app.reminder.ReminderManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import com.xiaozhitiao.app.reminder.ReminderUtil

// 数据类，代表一个笔记项
data class NoteItem(
    val id: Int, 
    var title: String, 
    var content: String, 
    val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()), 
    var completed: Boolean = false,
    var backgroundColor: String = "default",
    var completedDate: String? = null, // 添加完成时间字段
    var hasReminder: Boolean = false,
    var reminderTime: Long? = null,
    var reminderMessage: String? = null
)

// 创建CompositionLocal变量来存储自定义颜色RGB值
val LocalCustomRedValue = compositionLocalOf { 127f }
val LocalCustomGreenValue = compositionLocalOf { 127f }
val LocalCustomBlueValue = compositionLocalOf { 127f }

// 为所有预设颜色添加CompositionLocal值
val LocalRedColorValues = compositionLocalOf { Triple(230f, 77f, 77f) } // 红色
val LocalYellowColorValues = compositionLocalOf { Triple(230f, 204f, 51f) } // 黄色
val LocalBlueColorValues = compositionLocalOf { Triple(51f, 140f, 230f) } // 蓝色
val LocalGreenColorValues = compositionLocalOf { Triple(51f, 204f, 102f) } // 绿色
val LocalPurpleColorValues = compositionLocalOf { Triple(204f, 102f, 230f) } // 紫色

class MainActivity : ComponentActivity() {
    lateinit var noteRepository: NoteRepository
    private lateinit var reminderManager: ReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "note_database"
        )
        .fallbackToDestructiveMigration()
        .build()

        noteRepository = NoteRepository(db.noteDao())
        reminderManager = ReminderManager(applicationContext)
        
        // 请求必要的权限
        requestRequiredPermissions()
        
        // 应用启动时重新设置所有提醒
        resetAllReminders()
        
        // 检查错过的提醒
        checkMissedReminders()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotepadApp(noteRepository)
                }
            }
        }
    }
    
    // 请求必要的权限
    private fun requestRequiredPermissions() {
        // 检查并请求通知权限（Android 13及以上需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
        
        // 检查精确闹钟权限（Android 12及以上需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!reminderManager.canScheduleExactAlarms()) {
                reminderManager.requestExactAlarmPermission(this)
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            REQUEST_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限已授予，重新设置所有提醒
                    resetAllReminders()
                } else {
                    // 权限被拒绝，可以显示提示或采取其他措施
                    android.widget.Toast.makeText(
                        this,
                        "通知权限被拒绝，提醒功能可能无法正常工作",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    // 重新设置所有提醒
    private fun resetAllReminders() {
        lifecycleScope.launch {
            val currentTime = System.currentTimeMillis()
            noteRepository.getUpcomingReminders(currentTime).collect { notes ->
                notes.forEach { note ->
                    if (note.hasReminder && note.reminderTime != null) {
                        val title = note.title.ifEmpty { "提醒" }
                        val message = note.reminderMessage ?: note.content.ifEmpty { "您有一项待办事项" }
                        reminderManager.scheduleReminder(note.id, note.reminderTime, title, message)
                        Log.d("MainActivity", "重新设置提醒: 笔记ID=${note.id}, 时间=${note.reminderTime}, 当前时间=$currentTime")
                    }
                }
            }
        }
    }
    
    // 检查错过的提醒
    private fun checkMissedReminders() {
        lifecycleScope.launch {
            // 延迟一段时间，确保数据库已经准备好
            delay(2000) // 增加延迟，确保有足够时间完成初始化
            
            val currentTime = System.currentTimeMillis()
            noteRepository.getAllReminders().collect { notes ->
                Log.d("MainActivity", "检查错过的提醒: 共有${notes.size}个提醒")
                val missedReminders = ReminderUtil.checkMissedReminders(notes, applicationContext)
                if (missedReminders.isNotEmpty()) {
                    Log.d("MainActivity", "发现${missedReminders.size}个错过的提醒")
                    // 可以在这里触发一个合并的通知，告知用户有多少错过的提醒
                }
            }
        }
    }
    
    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotepadApp(noteRepository: NoteRepository) {
    val notes by noteRepository.allNotes.collectAsState(initial = emptyList())
    val completedNotes by noteRepository.completedNotes.collectAsState(initial = emptyList())
    val activeNotes by noteRepository.activeNotes.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<NoteItem?>(null) }
    var isGridView by rememberSaveable { mutableStateOf(true) }
    var isSelectionMode by rememberSaveable { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<Int>() }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showCompletedNotes by rememberSaveable { mutableStateOf(false) }
    var selectedColorFilter by remember { mutableStateOf("all") }
    var showColorFilterMenu by remember { mutableStateOf(false) }
    var showColorEditDialog by remember { mutableStateOf(false) }
    var currentEditingColor by remember { mutableStateOf("default") }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var fontSize by rememberSaveable { mutableStateOf(16f) }
    var sortOrder by rememberSaveable { mutableStateOf("time") }
    var showAboutDialog by remember { mutableStateOf(false) } // Added state for About Dialog
    var showClearAllConfirmDialog by remember { mutableStateOf(false) } // Added state for Clear All confirmation dialog
    var showDeleteSelectedConfirmDialog by remember { mutableStateOf(false) } // 删除所选笔记确认对话框
    var showTrashView by remember { mutableStateOf(false) } // 新增状态，控制是否显示废纸篓

    // 添加列表和网格的状态对象
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    // 全局颜色值状态
    var customRedValue by rememberSaveable { mutableStateOf(127f) }
    var customGreenValue by rememberSaveable { mutableStateOf(127f) }
    var customBlueValue by rememberSaveable { mutableStateOf(127f) }
    
    // 预设颜色的RGB值
    var redColorValues by rememberSaveable { mutableStateOf(Triple(230f, 77f, 77f)) }
    var yellowColorValues by rememberSaveable { mutableStateOf(Triple(230f, 204f, 51f)) }
    var blueColorValues by rememberSaveable { mutableStateOf(Triple(51f, 140f, 230f)) }
    var greenColorValues by rememberSaveable { mutableStateOf(Triple(51f, 204f, 102f)) }
    var purpleColorValues by rememberSaveable { mutableStateOf(Triple(204f, 102f, 230f)) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 直接根据当前显示模式选择数据源
    val baseNotes = if (showCompletedNotes) {
        completedNotes
    } else {
        activeNotes
    }

    // 转换为NoteItem（如果不是NoteItem类型）
    val noteItems = baseNotes.map { note ->
        if (note is NoteItem) note else NoteItem(
            id = note.id,
            title = note.title,
            content = note.content,
            date = note.date,
            completed = note.completed,
            backgroundColor = note.backgroundColor,
            completedDate = note.completedDate,
            hasReminder = note.hasReminder,
            reminderTime = note.reminderTime,
            reminderMessage = note.reminderMessage
        )
    }

    // 根据搜索查询和颜色筛选笔记
    val filteredNotes = noteItems.filter {
        val matchesSearchQuery = it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
        val matchesColorFilter = selectedColorFilter == "all" || it.backgroundColor == selectedColorFilter
        matchesSearchQuery && matchesColorFilter
    }

    // 颜色排序顺序
    val colorSortOrder = mapOf(
        "purple" to 0,
        "red" to 1,
        "yellow" to 2,
        "blue" to 3,
        "green" to 4,
        "default" to 5
    )

    // 根据排序方式对笔记进行排序
    val sortedNotes = when (sortOrder) {
        "time" -> filteredNotes.sortedByDescending { note ->
            try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(note.date)
            } catch (e: Exception) {
                Date(0)
            }
        }
        "color" -> filteredNotes.sortedBy { note ->
            colorSortOrder[note.backgroundColor] ?: 6
        }
        else -> filteredNotes
    }

    // 解析TXT格式的笔记内容
    fun parseTxtContent(content: String): List<NoteItem> {
        val notes = mutableListOf<NoteItem>()
        val noteBlocks = content.split("----------").filter { it.isNotBlank() }
        
        for (block in noteBlocks) {
            try {
                val lines = block.trim().lines()
                if (lines.size < 3) continue // 至少需要标题、日期和内容
                
                var title = ""
                var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                var completed = false
                var completedDate: String? = null
                var backgroundColor = "default"
                var content = ""
                var inContent = false
                
                for (line in lines) {
                    when {
                        line.startsWith("标题: ") -> title = line.substring(4).trim()
                        line.startsWith("时间: ") -> date = line.substring(4).trim()
                        line.startsWith("状态: ") -> completed = line.substring(4).trim() == "已完成"
                        line.startsWith("完成时间: ") -> completedDate = line.substring(6).trim().takeIf { it.isNotBlank() }
                        line.startsWith("背景色: ") -> backgroundColor = line.substring(5).trim()
                        line.startsWith("内容:") -> inContent = true
                        inContent -> content += if (content.isEmpty()) line.trim() else "\n${line.trim()}"
                    }
                }
                
                if (title.isNotBlank()) {
                    notes.add(
                        NoteItem(
                            id = 0, // 新ID将由Room分配
                            title = title, 
                            content = content,
                            date = date,
                            completed = completed,
                            backgroundColor = backgroundColor,
                            completedDate = completedDate,
                            hasReminder = false,
                            reminderTime = null,
                            reminderMessage = null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("TXT_PARSE", "Error parsing note block", e)
                // 继续处理下一个笔记块
            }
        }
        
        return notes
    }
    
    // 修改CSV解析函数，修复颜色还原和完成时间问题
    fun parseCsvContent(content: String): List<NoteItem> {
        val notes = mutableListOf<NoteItem>()
        val lines = content.lines().filter { it.isNotBlank() }
        
        if (lines.isEmpty()) return notes
        
        // 跳过标题行
        for (i in 1 until lines.size) {
            try {
                // 内联解析CSV逻辑
                val line = lines[i]
                if (line.isBlank()) continue
                
                // 处理CSV格式，考虑引号内可能有逗号的情况
                val values = mutableListOf<String>()
                var currentValue = StringBuilder()
                var inQuotes = false
                
                for (char in line) {
                    when {
                        char == '\"' -> inQuotes = !inQuotes
                        char == ',' && !inQuotes -> {
                            values.add(currentValue.toString())
                            currentValue = StringBuilder()
                        }
                        else -> currentValue.append(char)
                    }
                }
                
                // 添加最后一个值
                values.add(currentValue.toString())
                
                // 确保至少有基本字段
                if (values.size < 5) continue
                
                try {
                    val id = values[0].trim().removeSurrounding("\"").toIntOrNull() ?: 0
                    val title = values[1].trim().removeSurrounding("\"")
                    val content = values[2].trim().removeSurrounding("\"")
                    val date = values[3].trim().removeSurrounding("\"")
                    val completedStr = values[4].trim().removeSurrounding("\"")
                    val completed = completedStr == "是" || completedStr == "true" || completedStr == "True" || completedStr == "YES" || completedStr == "Yes"
                    
                    // 只有在笔记已完成时才设置完成时间
                    val completedDate = if (completed && values.size > 5 && values[5].trim().isNotEmpty()) {
                        values[5].trim().removeSurrounding("\"")
                    } else {
                        null
                    }
                    
                    // 确保正确设置背景颜色
                    val backgroundColor = if (values.size > 6 && values[6].trim().isNotEmpty()) {
                        val colorStr = values[6].trim().removeSurrounding("\"")
                        // 检查是否是有效的预设颜色
                        when (colorStr) {
                            "red", "yellow", "blue", "green", "purple", "default", "custom" -> colorStr
                            // 如果不是预设颜色，则设置为默认
                            else -> "default"
                        }
                    } else {
                        "default"
                    }
                    
                    println("导入CSV笔记: 标题=$title, 完成=$completed, 完成时间=$completedDate, 背景色=$backgroundColor")
                    
                    notes.add(NoteItem(
                        id = 0, // 新ID将由Room生成
                        title = title,
                        content = content,
                        date = date,
                        completed = completed,
                        backgroundColor = backgroundColor,
                        completedDate = completedDate,
                        hasReminder = false,
                        reminderTime = null,
                        reminderMessage = null
                    ))
                } catch (e: Exception) {
                    Log.e("CSV_PARSE", "Error creating note from values", e)
                    // 继续处理下一行
                }
            } catch (e: Exception) {
                Log.e("CSV_PARSE", "Error parsing line: ${lines[i]}", e)
                // 继续处理下一行
            }
        }
        
        return notes
    }

    // 文件选择器定义
    val openTxtFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: android.net.Uri? ->
            uri?.let { fileUri ->
                coroutineScope.launch {
                    try {
                        // 在协程内读取文件内容
                        val importedNotes = withContext(Dispatchers.IO) {
                            val contentResolver = context.contentResolver
                            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                                val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
                                val content = reader.readText()
                                
                                // 解析TXT内容
                                parseTxtContent(content)
                            } ?: emptyList()
                        }
                        
                        if (importedNotes.isNotEmpty()) {
                            withContext(Dispatchers.IO) {
                                for (note in importedNotes) {
                                    val entity = NoteEntity(
                                        0,
                                        note.title,
                                        note.content,
                                        note.date,
                                        note.completed,
                                        note.backgroundColor,
                                        note.completedDate,
                                        note.hasReminder,
                                        note.reminderTime,
                                        note.reminderMessage
                                    )
                                    noteRepository.insertNote(entity)
                                }
                            }
                            
                            // 显示导入成功提示
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "成功导入 ${importedNotes.size} 条笔记",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "未找到有效笔记数据",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(
                                context,
                                "导入失败: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    )
    
    val openCsvFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: android.net.Uri? ->
            uri?.let { fileUri ->
                coroutineScope.launch {
                    try {
                        // 在协程内读取文件内容
                        val importedNotes = withContext(Dispatchers.IO) {
                            val contentResolver = context.contentResolver
                            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                                val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
                                val content = reader.readText()
                                
                                // 解析CSV内容
                                parseCsvContent(content)
                            } ?: emptyList()
                        }
                        
                        if (importedNotes.isNotEmpty()) {
                            withContext(Dispatchers.IO) {
                                for (note in importedNotes) {
                                    val entity = NoteEntity(
                                        0,
                                        note.title,
                                        note.content,
                                        note.date,
                                        note.completed,
                                        note.backgroundColor,
                                        note.completedDate,
                                        note.hasReminder,
                                        note.reminderTime,
                                        note.reminderMessage
                                    )
                                    noteRepository.insertNote(entity)
                                }
                            }
                            
                            // 显示导入成功提示
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "成功导入 ${importedNotes.size} 条笔记",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "未找到有效笔记数据",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(
                                context,
                                "导入失败: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    )

    // 在应用启动时检查数据库中的completedDate字段
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val dao = (noteRepository as? NoteRepository)?.getDao()
                val firstNote = dao?.getFirstNote()
                println("数据库检查 - 第一条笔记: ${firstNote?.id}, 标题: ${firstNote?.title}, 完成: ${firstNote?.completed}, 完成时间: ${firstNote?.completedDate}")
                
                // 使用collectAsState的结果获取已完成笔记
                val completedNotesSnapshot = completedNotes
                println("数据库检查 - 已完成笔记数量: ${completedNotesSnapshot.size}")
                completedNotesSnapshot.forEach { note ->
                    println("数据库检查 - 已完成笔记: ID=${note.id}, 标题=${note.title}, 完成时间=${note.completedDate}")
                }
            } catch (e: Exception) {
                println("数据库检查失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // 打开编辑对话框的函数
    val onEditNoteClick = { note: NoteItem ->
        selectedNote = note
        println("打开编辑对话框，即将编辑笔记: ID=${note.id}, 标题=${note.title}")
        showEditDialog = true
    }

    // 更新笔记的函数
    val onUpdateNote = { updatedNote: NoteItem ->
        coroutineScope.launch {
            println("保存更新的笔记: ID=${updatedNote.id}, 标题=${updatedNote.title}, 完成=${updatedNote.completed}, 完成时间=${updatedNote.completedDate}")
            val entity = NoteEntity(
                updatedNote.id, 
                updatedNote.title, 
                updatedNote.content, 
                updatedNote.date, 
                updatedNote.completed, 
                updatedNote.backgroundColor,
                updatedNote.completedDate,
                updatedNote.hasReminder,
                updatedNote.reminderTime,
                updatedNote.reminderMessage
            )
            noteRepository.updateNote(entity)
            println("笔记已保存到数据库: ID=${entity.id}, 完成时间=${entity.completedDate}")
        }
        showEditDialog = false
        selectedNote = null
    }

    // 删除笔记的函数
    val onDeleteSelectedNotes: () -> Unit = {
        coroutineScope.launch {
            val idsToRemove = selectedNotes.toList()
            noteRepository.softDeleteNotesByIds(idsToRemove)
            selectedNotes.clear()
            isSelectionMode = false
        }
    }

    // 切换笔记选择状态
    val onToggleNoteSelection: (Int) -> Unit = { noteId ->
        if (selectedNotes.contains(noteId)) {
            selectedNotes.remove(noteId)
        } else {
            selectedNotes.add(noteId)
        }
    }

    // --- Export Logic ---
    // Function to format notes to CSV (Excel compatible)
    fun exportNotesToCsv(notesToExport: List<NoteItem>): String {
        val builder = StringBuilder()
        builder.append("\uFEFF")  // UTF-8 BOM
        builder.append("\"ID\",\"标题\",\"内容\",\"创建日期\",\"是否完成\",\"完成日期\",\"背景颜色\"\n")

        fun escapeCsvField(field: String?): String {
            val safeField = field ?: ""
            if (safeField.contains(",") || safeField.contains("\n") || safeField.contains("\"")) {
                return "\"${safeField.replace("\"", "\"\"")}\""
            }
            return safeField
        }

        notesToExport.forEach { note ->
            builder.append(escapeCsvField(note.id.toString()))
            builder.append(",")
            builder.append(escapeCsvField(note.title))
            builder.append(",")
            builder.append(escapeCsvField(note.content))
            builder.append(",")
            builder.append(escapeCsvField(note.date))
            builder.append(",")
            builder.append(escapeCsvField(if (note.completed) "是" else "否"))
            builder.append(",")
            // 对于未完成笔记，明确导出空的完成时间
            builder.append(escapeCsvField(if (note.completed) note.completedDate ?: "" else ""))
            builder.append(",")
            // 确保颜色值是有效的预设颜色
            val validColor = when (note.backgroundColor) {
                "red", "yellow", "blue", "green", "purple", "default", "custom" -> note.backgroundColor
                else -> "default"
            }
            builder.append(escapeCsvField(validColor))
            builder.append("\n")
        }
        return builder.toString()
    }

    fun exportNotesToTxt(notesToExport: List<NoteItem>): String {
        val builder = StringBuilder()
        notesToExport.forEach { note ->
            builder.append("标题: ${note.title}\n")
            builder.append("时间: ${note.date}\n")
            builder.append("状态: ${if (note.completed) "已完成" else "未完成"}\n")
            // 仅在笔记已完成时添加完成时间
            if (note.completed) {
                builder.append("完成时间: ${note.completedDate ?: ""}\n")
            }
            builder.append("背景色: ${note.backgroundColor}\n")
            builder.append("内容:\n${note.content}\n")
            builder.append("----------\n\n")
        }
        return builder.toString()
    }

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri: android.net.Uri? ->
            uri?.let { fileUri ->
                coroutineScope.launch {
                    try {
                        // 准备导出数据
                        val allNotesToExport = if (showCompletedNotes) {
                            // 如果当前显示的是已完成笔记，则获取所有笔记（已完成+未完成）
                            val combinedNotes = mutableListOf<NoteItem>()
                            combinedNotes.addAll(
                                activeNotes.map { 
                                    NoteItem(it.id, it.title, it.content, it.date, it.completed, it.backgroundColor, it.completedDate, it.hasReminder, it.reminderTime, it.reminderMessage) 
                                }
                            )
                            combinedNotes.addAll(noteItems) // 当前已显示的已完成笔记
                            combinedNotes
                        } else {
                            // 如果当前显示的是未完成笔记，则获取所有笔记（已完成+未完成）
                            val combinedNotes = mutableListOf<NoteItem>()
                            combinedNotes.addAll(noteItems) // 当前已显示的未完成笔记
                            combinedNotes.addAll(
                                completedNotes.map { 
                                    NoteItem(it.id, it.title, it.content, it.date, it.completed, it.backgroundColor, it.completedDate, it.hasReminder, it.reminderTime, it.reminderMessage) 
                                }
                            )
                            combinedNotes
                        }

                        val txtContent = exportNotesToTxt(allNotesToExport)
                        val contentWithBom = "\uFEFF" + txtContent
                        
                        // 在协程内部执行文件IO操作
                        withContext(Dispatchers.IO) {
                            val outputStream = context.contentResolver.openOutputStream(fileUri)
                            if (outputStream != null) {
                                try {
                                    OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                                        writer.write(contentWithBom)
                                        writer.flush()
                                    }
                                    println("Notes exported successfully to: $fileUri")
                                } catch (e: Exception) {
                                    println("Error writing to file: ${e.message}")
                                    e.printStackTrace()
                                } finally {
                                    try {
                                        outputStream.close()
                                    } catch (e: Exception) {
                                        // 忽略关闭时的错误
                                    }
                                }
                            } else {
                                println("Could not open output stream for: $fileUri")
                            }
                        }
                    } catch (e: Exception) {
                        println("Error exporting notes: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    )

    val createCsvFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: android.net.Uri? ->
            uri?.let { fileUri ->
                coroutineScope.launch {
                    try {
                        // 准备导出数据
                        val allNotesToExport = if (showCompletedNotes) {
                            // 如果当前显示的是已完成笔记，则获取所有笔记（已完成+未完成）
                            val combinedNotes = mutableListOf<NoteItem>()
                            combinedNotes.addAll(
                                activeNotes.map { 
                                    NoteItem(it.id, it.title, it.content, it.date, it.completed, it.backgroundColor, it.completedDate, it.hasReminder, it.reminderTime, it.reminderMessage) 
                                }
                            )
                            combinedNotes.addAll(noteItems) // 当前已显示的已完成笔记
                            combinedNotes
                        } else {
                            // 如果当前显示的是未完成笔记，则获取所有笔记（已完成+未完成）
                            val combinedNotes = mutableListOf<NoteItem>()
                            combinedNotes.addAll(noteItems) // 当前已显示的未完成笔记
                            combinedNotes.addAll(
                                completedNotes.map { 
                                    NoteItem(it.id, it.title, it.content, it.date, it.completed, it.backgroundColor, it.completedDate, it.hasReminder, it.reminderTime, it.reminderMessage) 
                                }
                            )
                            combinedNotes
                        }

                        val csvContent = exportNotesToCsv(allNotesToExport)
                        
                        // 在协程内部执行文件IO操作
                        withContext(Dispatchers.IO) {
                            val outputStream = context.contentResolver.openOutputStream(fileUri)
                            if (outputStream != null) {
                                try {
                                    OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                                        writer.write(csvContent)
                                        writer.flush()
                                    }
                                    println("Notes exported successfully as CSV to: $fileUri")
                                } catch (e: Exception) {
                                    println("Error writing to CSV file: ${e.message}")
                                    e.printStackTrace()
                                } finally {
                                    try {
                                        outputStream.close()
                                    } catch (e: Exception) {
                                        // 忽略关闭时的错误
                                    }
                                }
                            } else {
                                println("Could not open output stream for CSV: $fileUri")
                            }
                        }
                    } catch (e: Exception) {
                        println("Error exporting notes as CSV: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    )

    // 使用CompositionLocalProvider提供全局颜色值
    CompositionLocalProvider(
        LocalCustomRedValue provides customRedValue,
        LocalCustomGreenValue provides customGreenValue,
        LocalCustomBlueValue provides customBlueValue,
        LocalRedColorValues provides redColorValues,
        LocalYellowColorValues provides yellowColorValues,
        LocalBlueColorValues provides blueColorValues,
        LocalGreenColorValues provides greenColorValues,
        LocalPurpleColorValues provides purpleColorValues
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        navigationIcon = { 
                            // 在已完成笔记页面显示返回按钮
                            if (showCompletedNotes) {
                                IconButton(onClick = { showCompletedNotes = false }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "返回未完成笔记"
                                    )
                                }
                            } else {
                                // 将排序下拉菜单改为直接切换按钮
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                            onClick = {
                                            // 直接切换排序方式
                                            sortOrder = if (sortOrder == "time") "color" else "time"
                                                // 立即滚动到顶部
                                                coroutineScope.launch {
                                                    if (isGridView) {
                                                        gridState.animateScrollToItem(0)
                                                    } else {
                                                        listState.animateScrollToItem(0)
                                                    }
                                                }
                                            },
                                        modifier = Modifier
                                            .background(
                                                if (sortOrder == "time") 
                                                    MaterialTheme.colorScheme.surface
                                                else 
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                shape = androidx.compose.foundation.shape.CircleShape
                                            )
                                    ) {
                                                    Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Sort,
                                            contentDescription = if (sortOrder == "time") "按时间排序" else "按颜色排序",
                                            tint = if (sortOrder == "time") 
                                                MaterialTheme.colorScheme.onSurface
                                            else 
                                                MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                    Text(
                                        text = if (sortOrder == "time") "时间" else "颜色",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 2.dp)
                                        )
                                }
                            }
                        },
                        title = {
                            if (showCompletedNotes) {
                                Text(
                                    text = "已完成",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "小纸条",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        actions = {
                            if (isSelectionMode) {
                                IconButton(
                                    onClick = { 
                                        if (selectedNotes.isNotEmpty()) {
                                            showDeleteSelectedConfirmDialog = true 
                                        }
                                    },
                                    enabled = selectedNotes.isNotEmpty()
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "删除所选")
                                }
                                IconButton(onClick = {
                                    isSelectionMode = false
                                    selectedNotes.clear()
                                }) {
                                    Icon(Icons.Filled.Close, contentDescription = "取消选择")
                                }
                                Box {
                                    IconButton(onClick = { showSettingsMenu = true }) {
                                        Icon(Icons.Default.Settings, contentDescription = "设置")
                                    }
                                    DropdownMenu(
                                        expanded = showSettingsMenu,
                                        onDismissRequest = { showSettingsMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("删除") },
                                            onClick = {
                                                showSettingsMenu = false
                                                isSelectionMode = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("编辑全局颜色设置") },
                                            onClick = {
                                                showSettingsMenu = false
                                                currentEditingColor = "red"  // 默认选中红色进行编辑
                                                showColorEditDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("字体大小设置") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showFontSizeDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("废纸篓") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showTrashView = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("导入导出") },
                                            onClick = {
                                                showSettingsMenu = false
                                                // 显示导入导出子菜单
                                                val items = arrayOf("导出笔记 (TXT)", "导出笔记 (Excel)", "导入笔记 (TXT)", "导入笔记 (Excel)")
                                                val builder = android.app.AlertDialog.Builder(context)
                                                builder.setTitle("导入导出")
                                                builder.setItems(items) { _, which ->
                                                    when (which) {
                                                        0 -> {
                                                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                                            createFileLauncher.launch("Notes_Export_$timestamp.txt")
                                                        }
                                                        1 -> {
                                                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                                            createCsvFileLauncher.launch("Notes_Export_$timestamp.csv")
                                                        }
                                                        2 -> openTxtFileLauncher.launch(arrayOf("text/plain"))
                                                        3 -> openCsvFileLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv"))
                                                    }
                                                }
                                                builder.show()
                                            }
                                        )
                                        DropdownMenuItem( // 添加关于选项
                                            text = { Text("关于") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showAboutDialog = true
                                            }
                                        )
                                        DropdownMenuItem( // 添加清空所有选项
                                            text = { Text("清空所有", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showSettingsMenu = false
                                                showClearAllConfirmDialog = true
                                            }
                                        )
                                    }
                                }
                            } else {
                                if (!isSearchExpanded) {
                                    IconButton(onClick = { isSearchExpanded = true }) {
                                        Icon(Icons.Default.Search, contentDescription = "搜索")
                                    }
                                }
                                IconButton(onClick = { isGridView = !isGridView }) {
                                    Icon(
                                        imageVector = if (isGridView) Icons.Filled.Apps else Icons.Filled.Menu,
                                        contentDescription = if (isGridView) "切换到列表视图" else "切换到网格视图"
                                    )
                                }
                                // 添加显示已完成按钮
                                IconButton(
                                    onClick = { showCompletedNotes = !showCompletedNotes },
                                    modifier = Modifier
                                        .background(
                                            if (showCompletedNotes) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surface,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (showCompletedNotes) Icons.Filled.Check else Icons.Outlined.Check,
                                        contentDescription = if (showCompletedNotes) "显示未完成笔记" else "显示已完成笔记",
                                        tint = if (showCompletedNotes) MaterialTheme.colorScheme.primary
                                              else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box {
                                    IconButton(onClick = { showSettingsMenu = true }) {
                                        Icon(Icons.Default.Settings, contentDescription = "设置")
                                    }
                                    DropdownMenu(
                                        expanded = showSettingsMenu,
                                        onDismissRequest = { showSettingsMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("删除") },
                                            onClick = {
                                                showSettingsMenu = false
                                                isSelectionMode = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("编辑全局颜色设置") },
                                            onClick = {
                                                showSettingsMenu = false
                                                currentEditingColor = "red"  // 默认选中红色进行编辑
                                                showColorEditDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("字体大小设置") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showFontSizeDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("废纸篓") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showTrashView = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("导入导出") },
                                            onClick = {
                                                showSettingsMenu = false
                                                // 显示导入导出子菜单
                                                val items = arrayOf("导出笔记 (TXT)", "导出笔记 (Excel)", "导入笔记 (TXT)", "导入笔记 (Excel)")
                                                val builder = android.app.AlertDialog.Builder(context)
                                                builder.setTitle("导入导出")
                                                builder.setItems(items) { _, which ->
                                                    when (which) {
                                                        0 -> {
                                                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                                            createFileLauncher.launch("Notes_Export_$timestamp.txt")
                                                        }
                                                        1 -> {
                                                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                                            createCsvFileLauncher.launch("Notes_Export_$timestamp.csv")
                                                        }
                                                        2 -> openTxtFileLauncher.launch(arrayOf("text/plain"))
                                                        3 -> openCsvFileLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv"))
                                                    }
                                                }
                                                builder.show()
                                            }
                                        )
                                        DropdownMenuItem( // 添加关于选项
                                            text = { Text("关于") },
                                            onClick = {
                                                showSettingsMenu = false
                                                showAboutDialog = true
                                            }
                                        )
                                        DropdownMenuItem( // 添加清空所有选项
                                            text = { Text("清空所有", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showSettingsMenu = false
                                                showClearAllConfirmDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                    
                    // 搜索栏
                    if (isSearchExpanded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                placeholder = { Text("搜索...") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "搜索")
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isSearchExpanded = false; searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "关闭搜索")
                                    }
                                }
                            )
                            
                            // 颜色筛选器按钮
                            Box {
                                IconButton(
                                    onClick = { showColorFilterMenu = true },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(4.dp)
                                        .background(
                                            when(selectedColorFilter) {
                                                "red" -> MaterialTheme.colorScheme.errorContainer.copy(red = 0.9f, green = 0.3f, blue = 0.3f, alpha = 0.9f)
                                                "yellow" -> MaterialTheme.colorScheme.secondaryContainer.copy(red = 0.9f, green = 0.8f, blue = 0.2f, alpha = 0.9f)
                                                "blue" -> MaterialTheme.colorScheme.tertiaryContainer.copy(red = 0.2f, green = 0.55f, blue = 0.9f, alpha = 0.9f)
                                                "green" -> MaterialTheme.colorScheme.primaryContainer.copy(red = 0.2f, green = 0.8f, blue = 0.4f, alpha = 0.9f)
                                                "purple" -> MaterialTheme.colorScheme.surfaceVariant.copy(red = 0.8f, green = 0.4f, blue = 0.9f, alpha = 0.9f)
                                                else -> MaterialTheme.colorScheme.surface
                                            },
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "颜色筛选",
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selectedColorFilter != "all") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showColorFilterMenu,
                                    onDismissRequest = { showColorFilterMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("所有颜色") },
                                        onClick = {
                                            selectedColorFilter = "all"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.errorContainer.copy(red = 0.9f, green = 0.3f, blue = 0.3f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("红色")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "red"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.secondaryContainer.copy(red = 0.9f, green = 0.8f, blue = 0.2f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("黄色")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "yellow"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.tertiaryContainer.copy(red = 0.2f, green = 0.55f, blue = 0.9f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("蓝色")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "blue"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.primaryContainer.copy(red = 0.2f, green = 0.8f, blue = 0.4f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("绿色")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "green"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.surfaceVariant.copy(red = 0.8f, green = 0.4f, blue = 0.9f),
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("紫色")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "purple"
                                            showColorFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                            shape = androidx.compose.foundation.shape.CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("默认")
                                            }
                                        },
                                        onClick = {
                                            selectedColorFilter = "default"
                                            showColorFilterMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (!isSelectionMode && !showCompletedNotes && !showTrashView) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(112.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                            ),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "添加笔记",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        ) { innerPadding ->
            if (showTrashView) {
                TrashView(
                    noteRepository = noteRepository,
                    onBack = { showTrashView = false },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
            val modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)
            if (isGridView) {
                NoteGrid(
                    notes = sortedNotes,
                    isSelectionMode = isSelectionMode,
                    selectedNotes = selectedNotes,
                    onToggleSelection = onToggleNoteSelection,
                    onNoteClick = onEditNoteClick,
                    modifier = modifier,
                    fontSize = fontSize,
                    isInCompletedSection = showCompletedNotes,
                    gridState = gridState
                )
            } else {
                NoteList(
                    notes = sortedNotes,
                    isSelectionMode = isSelectionMode,
                    selectedNotes = selectedNotes,
                    onToggleSelection = onToggleNoteSelection,
                    onNoteClick = onEditNoteClick,
                    modifier = modifier,
                    fontSize = fontSize,
                    isInCompletedSection = showCompletedNotes,
                    listState = listState
                )
                }
            }
        }

        if (showAddDialog) {
            AddNoteDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, content, completed, selectedColor, completedDate, hasReminder, reminderTime, reminderMessage ->
                    coroutineScope.launch {
                        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        println("保存新笔记: 标题=$title, 完成=$completed, 完成时间=$completedDate")
                        val entity = NoteEntity(0, title, content, date, completed, selectedColor, completedDate, hasReminder, reminderTime, reminderMessage)
                        noteRepository.insertNote(entity)
                        println("新笔记已保存到数据库: 完成时间=${entity.completedDate}")
                    }
                    showAddDialog = false
                }
            )
        }

        if (showEditDialog && selectedNote != null) {
            EditNoteDialog(
                note = selectedNote!!,
                onDismiss = {
                    showEditDialog = false
                    selectedNote = null
                },
                onSave = onUpdateNote,
                onDelete = { noteId ->
                    coroutineScope.launch {
                        noteRepository.softDeleteNotesByIds(listOf(noteId))
                    }
                    showEditDialog = false
                    selectedNote = null
                }
            )
        }

        if (showColorEditDialog) {
            ColorEditDialog(
                onDismiss = { showColorEditDialog = false },
                onSave = { colorName, r, g, b ->
                    // 保存全局颜色设置
                    when (colorName) {
                        "red" -> redColorValues = Triple(r, g, b)
                        "yellow" -> yellowColorValues = Triple(r, g, b)
                        "blue" -> blueColorValues = Triple(r, g, b)
                        "green" -> greenColorValues = Triple(r, g, b)
                        "purple" -> purpleColorValues = Triple(r, g, b)
                        "custom" -> {
                            customRedValue = r
                            customGreenValue = g
                            customBlueValue = b
                        }
                    }
                    
                    android.widget.Toast.makeText(context, "已保存颜色设置", android.widget.Toast.LENGTH_SHORT).show()
                    showColorEditDialog = false
                },
                currentEditingColor = currentEditingColor,
                initialRedValue = null,  // 让对话框根据选择的预设色自动初始化
                initialGreenValue = null,
                initialBlueValue = null,
                presetColors = mapOf(
                    "red" to redColorValues,
                    "yellow" to yellowColorValues,
                    "blue" to blueColorValues,
                    "green" to greenColorValues,
                    "purple" to purpleColorValues,
                    "default" to Triple(227f, 226f, 246f),
                    "custom" to Triple(customRedValue, customGreenValue, customBlueValue)
                )
            )
        }

        if (showFontSizeDialog) {
            FontSizeDialog(
                currentSize = fontSize,
                onDismiss = { showFontSizeDialog = false },
                onSave = { newSize ->
                    fontSize = newSize
                    showFontSizeDialog = false
                }
            )
        }

        if (showAboutDialog) { // Added AboutDialog call
            AboutDialog(onDismiss = { showAboutDialog = false })
        }
        
        if (showClearAllConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllConfirmDialog = false },
                title = { Text("确认清空") },
                text = { Text("确定要删除所有笔记吗？此操作不可撤销。") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                noteRepository.deleteAllNotes()
                                android.widget.Toast.makeText(
                                    context,
                                    "所有笔记已清空",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                            showClearAllConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("确认清空")
                    }
                },
                dismissButton = {
                    Button(onClick = { showClearAllConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        // --- Delete Confirmation Dialog ---
        if (showDeleteSelectedConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteSelectedConfirmDialog = false },
                title = { Text("确认删除") },
                text = { Text("确定要删除所选的 ${selectedNotes.size} 个笔记吗？") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteSelectedNotes()
                            showDeleteSelectedConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteSelectedConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun NoteList(
    notes: List<NoteItem>,
    isSelectionMode: Boolean,
    selectedNotes: List<Int>,
    onToggleSelection: (Int) -> Unit,
    onNoteClick: (NoteItem) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: Float = 16f,
    isInCompletedSection: Boolean = false,
    listState: LazyListState
) {
    // 对数据预处理，避免在LazyColumn中重复计算
    val preprocessed = remember(notes) {
        val result = mutableListOf<Pair<String, List<NoteItem>>>()
        
        // 不使用groupBy，手动遍历一次更高效
        var currentDate = ""
        var currentGroup = mutableListOf<NoteItem>()
        
        for (note in notes) {
            val noteDate = if (isInCompletedSection) {
                note.completedDate?.substringBefore(" ") ?: "未知日期"
            } else {
                note.date.substringBefore(" ")
            }
            
            if (noteDate != currentDate) {
                // 保存上一组
                if (currentDate.isNotEmpty() && currentGroup.isNotEmpty()) {
                    result.add(Pair(currentDate, currentGroup.toList()))
                }
                // 开始新一组
                currentDate = noteDate
                currentGroup = mutableListOf()
            }
            
            currentGroup.add(note)
        }
        
        // 添加最后一组
        if (currentDate.isNotEmpty() && currentGroup.isNotEmpty()) {
            result.add(Pair(currentDate, currentGroup.toList()))
        }
        
        result
    }
    
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true
    ) {
        preprocessed.forEach { (date, notesForDate) ->
            item(key = "header_$date") {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )
            }
            
            items(
                items = notesForDate, 
                key = { it.id },
                contentType = { "note" } // 添加contentType提示给LazyColumn优化渲染
            ) { note ->
                NoteCard(
                    note = note,
                    isSelectionMode = isSelectionMode,
                    isSelected = selectedNotes.contains(note.id),
                    onToggleSelection = { onToggleSelection(note.id) },
                    onClick = { onNoteClick(note) },
                    fontSize = fontSize,
                    isInCompletedSection = isInCompletedSection
                )
            }
        }
    }
}

@Composable
fun NoteGrid(
    notes: List<NoteItem>,
    isSelectionMode: Boolean,
    selectedNotes: List<Int>,
    onToggleSelection: (Int) -> Unit,
    onNoteClick: (NoteItem) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: Float = 16f,
    isInCompletedSection: Boolean = false,
    gridState: LazyGridState
) {
    val columns = 2
    
    // 使用remember避免不必要的重组
    val preparedNotes = remember(notes) { notes }
    
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true
    ) {
        items(
            items = preparedNotes, 
            key = { it.id },
            contentType = { "note" } // 添加contentType提示给LazyVerticalGrid优化渲染
        ) { note ->
            // 复用 key 由 items 的 key = { it.id } 提供；
            // items 的内容 lambda 作用域是 LazyGridItemScope，不存在 key() 函数，不能再嵌套 key {}
            NoteCard(
                note = note,
                isSelectionMode = isSelectionMode,
                isSelected = selectedNotes.contains(note.id),
                onToggleSelection = { onToggleSelection(note.id) },
                onClick = { onNoteClick(note) },
                fontSize = fontSize,
                isInCompletedSection = isInCompletedSection
            )
        }
    }
}

@Composable
fun NoteCard(
    note: NoteItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onClick: () -> Unit,
    fontSize: Float = 16f,
    isInCompletedSection: Boolean = false // 添加参数表明是否在已完成笔记界面
) {
    // Fetch LocalComposition values outside remember
    val localRed = LocalRedColorValues.current
    val localYellow = LocalYellowColorValues.current
    val localBlue = LocalBlueColorValues.current
    val localGreen = LocalGreenColorValues.current
    val localPurple = LocalPurpleColorValues.current
    val localCustomRed = LocalCustomRedValue.current
    val localCustomGreen = LocalCustomGreenValue.current
    val localCustomBlue = LocalCustomBlueValue.current
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    // 预定义背景颜色
    val backgroundColor = remember(note.backgroundColor, isSelected, note.completed, localRed, localYellow, localBlue, localGreen, localPurple, localCustomRed, localCustomGreen, localCustomBlue, primaryContainerColor, surfaceVariantColor) {
        when(note.backgroundColor) {
            "red" -> {
                val (r, g, b) = localRed
                androidx.compose.ui.graphics.Color(
                    red = r / 255f,
                    green = g / 255f,
                    blue = b / 255f
                )
            }
            "yellow" -> {
                val (r, g, b) = localYellow
                androidx.compose.ui.graphics.Color(
                    red = r / 255f,
                    green = g / 255f,
                    blue = b / 255f
                )
            }
            "blue" -> {
                val (r, g, b) = localBlue
                androidx.compose.ui.graphics.Color(
                    red = r / 255f,
                    green = g / 255f,
                    blue = b / 255f
                )
            }
            "green" -> {
                val (r, g, b) = localGreen
                androidx.compose.ui.graphics.Color(
                    red = r / 255f,
                    green = g / 255f,
                    blue = b / 255f
                )
            }
            "purple" -> {
                val (r, g, b) = localPurple
                androidx.compose.ui.graphics.Color(
                    red = r / 255f,
                    green = g / 255f,
                    blue = b / 255f
                )
            }
            "custom" -> {
                androidx.compose.ui.graphics.Color(
                    red = localCustomRed / 255f,
                    green = localCustomGreen / 255f,
                    blue = localCustomBlue / 255f
                )
            }
            else -> {
                if (isSelected) {
                    primaryContainerColor
                } else {
                    surfaceVariantColor
        }
    }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClick = {
                    if (isSelectionMode) {
                        onToggleSelection()
                    } else {
                        onClick()
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 在卡片顶部显示完成时间（如果有）
            if (note.completed) {
                println("尝试显示完成时间横幅: ${note.completedDate}")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) // 增加背景透明度
                        .padding(horizontal = 16.dp, vertical = 2.dp) // 减小垂直内边距
                ) {
                    Text(
                        text = note.completedDate ?: "未记录",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (fontSize - 2).sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
            
            // 在卡片顶部显示提醒信息（如果有）
            if (note.hasReminder == true && note.reminderTime != null) {
                val reminderTimeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(note.reminderTime!!))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "提醒: $reminderTimeFormatted",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = (fontSize - 2).sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = if (isSelectionMode) 8.dp else 0.dp)
                ) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = fontSize.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                        )
                    
                    // 如果是已完成的笔记，显示已完成标签
                    if (note.completed) {
                            Text(
                                text = "已完成",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = fontSize.sp, // 与标题相同的字体大小
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold // 加粗文本
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                    }
                    Spacer(modifier = Modifier.height(2.dp)) // 减小间距
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (fontSize - 2).sp
                        ),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp)) // 减小间距
                    
                    // 显示创建时间
                    Text(
                        text = note.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = (fontSize - 4).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelection() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onSave: (String, String, Boolean, String, String?, Boolean, Long?, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf("default") }
    var completedDate by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    
    // 提醒相关状态
    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var reminderMessage by remember { mutableStateOf<String?>(null) }
    var showReminderDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val reminderManager = remember { ReminderManager(context) }
    
    // 检查完成状态是否改变，如果变为已完成，记录时间
    LaunchedEffect(completed) {
        val oldCompletedDate = completedDate
        if (completed) {
            completedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            println("新笔记标记为已完成，完成时间=$completedDate")
        } else {
            completedDate = null
            println("新笔记标记为未完成，清除完成时间")
        }
        
        if (oldCompletedDate != completedDate) {
            println("新笔记完成时间已更新: 从 $oldCompletedDate 到 $completedDate")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "添加笔记",
                    style = MaterialTheme.typography.titleMedium
                    )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("已完成")
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it }
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .focusRequester(focusRequester)
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                        .focusRequester(contentFocusRequester)
                )
                
                // 颜色选择行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 默认颜色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "default") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "default" }
                )
                
                    // 红色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalRedColorValues.current.first / 255f,
                                    green = LocalRedColorValues.current.second / 255f,
                                    blue = LocalRedColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "red") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "red" }
                    )
                    
                    // 黄色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalYellowColorValues.current.first / 255f,
                                    green = LocalYellowColorValues.current.second / 255f,
                                    blue = LocalYellowColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "yellow") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "yellow" }
                    )
                    
                    // 蓝色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalBlueColorValues.current.first / 255f,
                                    green = LocalBlueColorValues.current.second / 255f,
                                    blue = LocalBlueColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "blue") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "blue" }
                    )
                    
                    // 绿色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalGreenColorValues.current.first / 255f,
                                    green = LocalGreenColorValues.current.second / 255f,
                                    blue = LocalGreenColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "green") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "green" }
                    )
                    
                    // 紫色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalPurpleColorValues.current.first / 255f,
                                    green = LocalPurpleColorValues.current.second / 255f,
                                    blue = LocalPurpleColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "purple") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "purple" }
                    )
                    
                    // 自定义颜色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalCustomRedValue.current / 255f,
                                    green = LocalCustomGreenValue.current / 255f,
                                    blue = LocalCustomBlueValue.current / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "custom") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "custom" }
                    )
                }
                
                // 提醒信息行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasReminder && reminderTime != null) {
                        val reminderTimeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(reminderTime!!))
                        
                        Text(
                            text = "提醒: $reminderTimeFormatted",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        // 取消提醒按钮
                        TextButton(
                            onClick = { 
                                hasReminder = false
                                reminderTime = null
                                reminderMessage = null
                            }
                        ) {
                            Text("取消提醒")
                        }
                    } else {
                        Text(
                            text = "未设置提醒",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        // 设置提醒按钮
                        TextButton(
                            onClick = { showReminderDialog = true }
                        ) {
                            Text("设置提醒")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            ) {
                    Text("取消")
            }
            Button(
                    onClick = {
                        onSave(title, content, completed, selectedColor, completedDate, hasReminder, reminderTime, reminderMessage)
                    }
            ) {
                    Text("保存")
                }
            }
        },
        dismissButton = null
    )
    
    // 提醒设置对话框
    if (showReminderDialog) {
        ReminderDateTimePickerDialog(
            onDismiss = { showReminderDialog = false },
            onConfirm = { selectedTimeMillis ->
                hasReminder = true
                reminderTime = selectedTimeMillis
                reminderMessage = title.ifEmpty { "提醒事项" }
                showReminderDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true, name = "List View")
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        val context = LocalContext.current
        NotepadApp(NoteRepository(NoteDatabase.getDatabase(context).noteDao()))
    }
}

@Preview(showBackground = true, name = "Grid View")
@Composable
fun GridPreview() {
    MyApplicationTheme {
        val context = LocalContext.current
        NotepadApp(NoteRepository(NoteDatabase.getDatabase(context).noteDao()))
    }
}

@Preview(showBackground = true, name = "Add Note Dialog")
@Composable
fun AddNoteDialogPreview() {
    MyApplicationTheme {
        AddNoteDialog(onDismiss = {}, onSave = { _, _, _, _, _, _, _, _ -> })
    }
}

@Composable
fun EditNoteDialog(
    note: NoteItem, 
    onDismiss: () -> Unit, 
    onSave: (NoteItem) -> Unit,
    onDelete: (Int) -> Unit
) {
    var title by rememberSaveable(note.id) { mutableStateOf(note.title) }
    var content by rememberSaveable(note.id) { mutableStateOf(note.content) }
    var completed by rememberSaveable(note.id) { mutableStateOf(note.completed) }
    var selectedColor by rememberSaveable(note.id) { mutableStateOf(note.backgroundColor) }
    var completedDate by rememberSaveable(note.id) { mutableStateOf(note.completedDate) }
    var creationDate by rememberSaveable(note.id) { mutableStateOf(note.date) } // 添加创建日期状态
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var datePickerType by remember { mutableStateOf("") } // "created" 或 "completed"
    val focusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val noteRepository = remember { (context as? MainActivity)?.noteRepository }
    var showEditHistoryDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // 提醒相关状态
    var hasReminder by rememberSaveable(note.id) { mutableStateOf(note.hasReminder) }
    var reminderTime by rememberSaveable(note.id) { mutableStateOf(note.reminderTime) }
    var reminderMessage by rememberSaveable(note.id) { mutableStateOf(note.reminderMessage) }
    var showReminderDialog by remember { mutableStateOf(false) }
    val reminderManager = remember { ReminderManager(context) }
    
    // 打印初始状态
    LaunchedEffect(Unit) {
        println("编辑对话框打开: ID=${note.id}, 初始完成状态=${note.completed}, 初始完成时间=${note.completedDate}")
        println("编辑对话框状态: 完成状态=$completed, 完成时间=$completedDate")
        
        // 如果笔记已完成但没有完成时间，给它添加一个时间
        if (completed && completedDate == null) {
            completedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            println("已完成笔记没有完成时间，添加当前时间: $completedDate")
        }
    }
    
    // 检查完成状态是否改变，如果从未完成变为已完成，记录时间
    LaunchedEffect(completed) {
        val oldCompletedDate = completedDate
        if (completed && completedDate == null) {
            completedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            println("笔记标记为已完成，完成时间=$completedDate")
        } else if (!completed) {
            completedDate = null
            println("笔记标记为未完成，清除完成时间")
        }
        
        if (oldCompletedDate != completedDate) {
            println("完成时间已更新: 从 $oldCompletedDate 到 $completedDate")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp) // 完全移除顶部和底部内边距
            ) {
                Text(
                    text = "编辑笔记",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp // 增大字体大小
                    )
                )
                // 优化已完成选项
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(0.dp) // 移除内边距
                ) {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp // 减小字体大小
                        )
                    )
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it },
                        modifier = Modifier
                            .scale(0.8f) // 缩小复选框
                            .padding(0.dp) // 移除内边距
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                // 标题输入框
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .focusRequester(focusRequester)
                )
                
                // 内容输入框
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                        .focusRequester(contentFocusRequester),
                    textStyle = TextStyle(fontSize = 14.sp)
                )
                
                // 颜色选择行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 默认颜色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "default") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "default" }
                    )
                    
                    // 红色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalRedColorValues.current.first / 255f,
                                    green = LocalRedColorValues.current.second / 255f,
                                    blue = LocalRedColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "red") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "red" }
                    )
                    
                    // 黄色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalYellowColorValues.current.first / 255f,
                                    green = LocalYellowColorValues.current.second / 255f,
                                    blue = LocalYellowColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "yellow") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "yellow" }
                    )
                    
                    // 蓝色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalBlueColorValues.current.first / 255f,
                                    green = LocalBlueColorValues.current.second / 255f,
                                    blue = LocalBlueColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "blue") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "blue" }
                    )
                    
                    // 绿色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalGreenColorValues.current.first / 255f,
                                    green = LocalGreenColorValues.current.second / 255f,
                                    blue = LocalGreenColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "green") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "green" }
                )
                
                    // 紫色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalPurpleColorValues.current.first / 255f,
                                    green = LocalPurpleColorValues.current.second / 255f,
                                    blue = LocalPurpleColorValues.current.third / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "purple") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "purple" }
                    )
                    
                    // 自定义颜色
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(
                                    red = LocalCustomRedValue.current / 255f,
                                    green = LocalCustomGreenValue.current / 255f,
                                    blue = LocalCustomBlueValue.current / 255f
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedColor == "custom") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedColor = "custom" }
                    )
                }
                
                // 日期和提醒信息行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "创建: ${creationDate.substringBefore(" ")}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { 
                            datePickerType = "created"
                            showDatePickerDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(24.dp) // 减小按钮高度
                    ) {
                        Text("修改", fontSize = 10.sp)
                    }
                    
                    // 添加修改历史按钮，放在右侧
                    TextButton(
                        onClick = { showEditHistoryDialog = true },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(24.dp) // 减小按钮高度
                    ) {
                        Text("历史", fontSize = 10.sp)
                    }
                }
                
                // 如果是已完成的笔记，显示完成时间
                if (completed && completedDate != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "完成: ${completedDate!!.substringBefore(" ")}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { 
                                datePickerType = "completed"
                                showDatePickerDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp) // 减小按钮高度
                        ) {
                            Text("修改", fontSize = 10.sp)
                        }
                    }
                }
                
                // 提醒信息行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasReminder && reminderTime != null) {
                        val reminderTimeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(reminderTime!!))
                        
                        Text(
                            text = "提醒: $reminderTimeFormatted",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // 取消提醒按钮
                        TextButton(
                            onClick = { 
                                hasReminder = false
                                reminderTime = null
                                reminderMessage = null
                                
                                // 取消系统提醒
                                reminderManager.cancelReminder(note.id)
                                
                                // 更新数据库
                                noteRepository?.let { repo ->
                                    coroutineScope.launch {
                                        repo.cancelReminder(note.id)
                                    }
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp) // 减小按钮高度
                        ) {
                            Text("取消", fontSize = 10.sp)
                        }
                    } else {
                        Text(
                            text = "未设置提醒",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // 设置提醒按钮
                        TextButton(
                            onClick = { showReminderDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("设置", fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 删除按钮
                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), // 减小按钮内边距
                    modifier = Modifier.weight(1f)
                ) {
                    Text("删除", fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 保存按钮
                Button(
                    onClick = {
                        val updatedNote = note.copy(
                            title = title, 
                            content = content, 
                            date = creationDate, // 使用修改后的创建时间
                            completed = completed,
                            backgroundColor = selectedColor,
                            completedDate = completedDate,
                            hasReminder = hasReminder,
                            reminderTime = reminderTime,
                            reminderMessage = reminderMessage
                        )
                        println("准备保存更新的笔记: ID=${updatedNote.id}, 完成=${updatedNote.completed}, 完成时间=${updatedNote.completedDate}")
                        onSave(updatedNote)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), // 减小按钮内边距
                    modifier = Modifier.weight(1f)
                ) {
                    Text("保存", fontSize = 14.sp)
                }
            }
        },
        dismissButton = { /* 移除原有的dismissButton */ },
        modifier = Modifier.padding(4.dp) // 减小整个对话框的内边距
    )

    // 日期选择对话框
    if (showDatePickerDialog) {
        DateTimePickerDialog(
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { selectedDate ->
                when (datePickerType) {
                    "created" -> {
                        // 记录修改历史
                        noteRepository?.let { repo ->
                            coroutineScope.launch {
                                repo.addEditHistory(
                                    noteId = note.id,
                                    editType = "DATE_CREATED",
                                    oldValue = creationDate,
                                    newValue = selectedDate
                                )
                            }
                        }
                        creationDate = selectedDate
                    }
                    "completed" -> {
                        // 记录修改历史
                        noteRepository?.let { repo ->
                            coroutineScope.launch {
                                repo.addEditHistory(
                                    noteId = note.id,
                                    editType = "DATE_COMPLETED",
                                    oldValue = completedDate,
                                    newValue = selectedDate
                                )
                            }
                        }
                        completedDate = selectedDate
                    }
                }
                showDatePickerDialog = false
            },
            initialDateTime = when (datePickerType) {
                "created" -> creationDate
                "completed" -> completedDate ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                else -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            }
        )
    }
    
    // 修改历史对话框
    if (showEditHistoryDialog && noteRepository != null) {
        EditHistoryDialog(
            noteId = note.id,
            noteRepository = noteRepository,
            onDismiss = { showEditHistoryDialog = false }
        )
    }

    // 提醒设置对话框
    if (showReminderDialog) {
        ReminderDateTimePickerDialog(
            onDismiss = { showReminderDialog = false },
            onConfirm = { selectedTimeMillis ->
                hasReminder = true
                reminderTime = selectedTimeMillis
                reminderMessage = title.ifEmpty { "提醒事项" }
                
                // 设置系统提醒
                val noteTitle = title.ifEmpty { "提醒" }
                val message = content.ifEmpty { "您有一项待办事项" }
                reminderManager.scheduleReminder(note.id, selectedTimeMillis, noteTitle, message)
                
                // 更新数据库
                noteRepository?.let { repo ->
                    coroutineScope.launch {
                        repo.setReminder(note.id, selectedTimeMillis, message)
                    }
                }
                
                showReminderDialog = false
            }
        )
    }

    // 删除确认对话框
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个笔记吗？此操作不可撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(note.id)
                        showDeleteConfirmDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun ColorPicker(
    selectedColor: String, 
    onColorSelected: (String) -> Unit,
    compact: Boolean = false // 紧凑模式参数
) {
    val colors = listOf(
        "default" to MaterialTheme.colorScheme.surfaceVariant,
        "red" to MaterialTheme.colorScheme.errorContainer.copy(red = 0.9f, green = 0.3f, blue = 0.3f),
        "yellow" to MaterialTheme.colorScheme.secondaryContainer.copy(red = 0.9f, green = 0.8f, blue = 0.2f),
        "blue" to MaterialTheme.colorScheme.tertiaryContainer.copy(red = 0.2f, green = 0.55f, blue = 0.9f, alpha = 0.9f),
        "green" to MaterialTheme.colorScheme.primaryContainer.copy(red = 0.2f, green = 0.8f, blue = 0.4f, alpha = 0.9f),
        "purple" to MaterialTheme.colorScheme.surfaceVariant.copy(red = 0.8f, green = 0.4f, blue = 0.9f)
    )

    val boxSize = if (compact) 28.dp else 40.dp // 进一步减小颜色框大小
    val textStyle = if (compact) 
                    MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp) // 更小的文本大小
                    else MaterialTheme.typography.bodyMedium

    Column(
        verticalArrangement = if (compact) Arrangement.Center else Arrangement.Top,
        modifier = Modifier.padding(bottom = if (compact) 0.dp else 8.dp)
    ) {
        Text(
            text = "背景颜色",
            style = textStyle,
            modifier = Modifier.padding(bottom = if (compact) 2.dp else 8.dp) // 减小底部内边距
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            colors.forEach { (colorName, colorValue) ->
                val color = colorValue
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .padding(if (compact) 1.dp else 4.dp) // 减小内边距
                        .clickable { 
                            onColorSelected(colorName)
                            // 打印颜色值到日志
                            val red = (color.red * 255f).toInt()
                            val green = (color.green * 255f).toInt()
                            val blue = (color.blue * 255f).toInt()
                            println("颜色名称: $colorName, RGB值: ($red, $green, $blue), HEX: #${red.toString(16).padStart(2, '0')}${green.toString(16).padStart(2, '0')}${blue.toString(16).padStart(2, '0')}")
                        }
                        .background(
                            color = colorValue,
                            shape = if (selectedColor == colorName) 
                                    androidx.compose.foundation.shape.CircleShape 
                                    else androidx.compose.foundation.shape.RoundedCornerShape(if (compact) 2.dp else 4.dp)
                        )
                        .border(
                            width = if (selectedColor == colorName) (1.5).dp else (0.5).dp, // 使用标准dp语法
                            color = if (selectedColor == colorName) 
                                   MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.outline,
                            shape = if (selectedColor == colorName) 
                                   androidx.compose.foundation.shape.CircleShape 
                                   else androidx.compose.foundation.shape.RoundedCornerShape(if (compact) 2.dp else 4.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun ColorEditDialog(
    onDismiss: () -> Unit, 
    onSave: (String, Float, Float, Float) -> Unit, 
    currentEditingColor: String, 
    initialRedValue: Float?, 
    initialGreenValue: Float?, 
    initialBlueValue: Float?,
    presetColors: Map<String, Triple<Float, Float, Float>>
) {
    var selectedPresetColor by remember { mutableStateOf(currentEditingColor) }
    var redValue by remember { mutableStateOf(initialRedValue ?: 0f) }
    var greenValue by remember { mutableStateOf(initialGreenValue ?: 0f) }
    var blueValue by remember { mutableStateOf(initialBlueValue ?: 0f) }
    var customColorName by remember { mutableStateOf("custom") }
    var isCustomColor by remember { mutableStateOf(false) }
    
    // 初始化RGB值
    LaunchedEffect(selectedPresetColor) {
        if (presetColors.containsKey(selectedPresetColor)) {
            val (r, g, b) = presetColors[selectedPresetColor]!!
            redValue = r
            greenValue = g
            blueValue = b
            isCustomColor = false
        }
    }
    
    // 计算当前RGB颜色
    val currentColor = androidx.compose.ui.graphics.Color(
        red = redValue / 255f,
        green = greenValue / 255f,
        blue = blueValue / 255f
    )
    
    // 将RGB值转换为十六进制
    val hexColor = "#${redValue.toInt().toString(16).padStart(2, '0')}${greenValue.toInt().toString(16).padStart(2, '0')}${blueValue.toInt().toString(16).padStart(2, '0')}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("编辑颜色")
            }
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                // 颜色预览
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(color = currentColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 预设颜色选择器
                Text(
                    text = "选择要编辑的预设颜色",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    presetColors.forEach { (colorName, _) ->
                        val presetColor = when(colorName) {
                            "red" -> {
                                val (r, g, b) = presetColors["red"]!!
                                androidx.compose.ui.graphics.Color(
                                    red = r / 255f,
                                    green = g / 255f,
                                    blue = b / 255f
                                )
                            }
                            "yellow" -> {
                                val (r, g, b) = presetColors["yellow"]!!
                                androidx.compose.ui.graphics.Color(
                                    red = r / 255f,
                                    green = g / 255f,
                                    blue = b / 255f
                                )
                            }
                            "blue" -> {
                                val (r, g, b) = presetColors["blue"]!!
                                androidx.compose.ui.graphics.Color(
                                    red = r / 255f,
                                    green = g / 255f,
                                    blue = b / 255f
                                )
                            }
                            "green" -> {
                                val (r, g, b) = presetColors["green"]!!
                                androidx.compose.ui.graphics.Color(
                                    red = r / 255f,
                                    green = g / 255f,
                                    blue = b / 255f
                                )
                            }
                            "purple" -> {
                                val (r, g, b) = presetColors["purple"]!!
                                androidx.compose.ui.graphics.Color(
                                    red = r / 255f,
                                    green = g / 255f,
                                    blue = b / 255f
                                )
                            }
                            "default" -> MaterialTheme.colorScheme.surfaceVariant
                            "custom" -> androidx.compose.ui.graphics.Color(
                                red = LocalCustomRedValue.current / 255f,
                                green = LocalCustomGreenValue.current / 255f,
                                blue = LocalCustomBlueValue.current / 255f
                            )
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .clickable { 
                                    selectedPresetColor = colorName
                                    customColorName = colorName
                                    isCustomColor = false
                                }
                                .background(
                                    color = presetColor,
                                    shape = if (selectedPresetColor == colorName) 
                                            androidx.compose.foundation.shape.CircleShape 
                                            else androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = if (selectedPresetColor == colorName) (2.0).dp else (1.0).dp, // 使用标准dp语法
                                    color = if (selectedPresetColor == colorName) 
                                           MaterialTheme.colorScheme.primary 
                                           else MaterialTheme.colorScheme.outline,
                                    shape = if (selectedPresetColor == colorName) 
                                           androidx.compose.foundation.shape.CircleShape 
                                           else androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // RGB滑块
                Text(
                    text = "自定义RGB颜色 ($hexColor)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 红色滑块
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("红色 (R):", modifier = Modifier.width(60.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        var redText by remember { mutableStateOf(redValue.toInt().toString()) }
                        // 当滑块值变化时更新文本框
                        LaunchedEffect(redValue) {
                            redText = redValue.toInt().toString()
                        }
                        OutlinedTextField(
                            value = redText,
                            onValueChange = { text ->
                                redText = text
                                text.toIntOrNull()?.let { 
                                    if (it in 0..255) redValue = it.toFloat()
                                }
                            },
                            modifier = Modifier.width(70.dp),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    redText.toIntOrNull()?.let { 
                                        if (it in 0..255) redValue = it.toFloat()
                                    }
                                }
                            ),
                            singleLine = true
                        )
                    }
                    Slider(
                        value = redValue,
                        onValueChange = { redValue = it },
                        valueRange = 0f..255f,
                        colors = SliderDefaults.colors(
                            thumbColor = androidx.compose.ui.graphics.Color.Red,
                            activeTrackColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 绿色滑块
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("绿色 (G):", modifier = Modifier.width(60.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        var greenText by remember { mutableStateOf(greenValue.toInt().toString()) }
                        // 当滑块值变化时更新文本框
                        LaunchedEffect(greenValue) {
                            greenText = greenValue.toInt().toString()
                        }
                        OutlinedTextField(
                            value = greenText,
                            onValueChange = { text ->
                                greenText = text
                                text.toIntOrNull()?.let { 
                                    if (it in 0..255) greenValue = it.toFloat()
                                }
                            },
                            modifier = Modifier.width(70.dp),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    greenText.toIntOrNull()?.let { 
                                        if (it in 0..255) greenValue = it.toFloat()
                                    }
                                }
                            ),
                            singleLine = true
                        )
                    }
                    Slider(
                        value = greenValue,
                        onValueChange = { greenValue = it },
                        valueRange = 0f..255f,
                        colors = SliderDefaults.colors(
                            thumbColor = androidx.compose.ui.graphics.Color.Green,
                            activeTrackColor = androidx.compose.ui.graphics.Color.Green.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 蓝色滑块
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("蓝色 (B):", modifier = Modifier.width(60.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        var blueText by remember { mutableStateOf(blueValue.toInt().toString()) }
                        // 当滑块值变化时更新文本框
                        LaunchedEffect(blueValue) {
                            blueText = blueValue.toInt().toString()
                        }
                        OutlinedTextField(
                            value = blueText,
                            onValueChange = { text ->
                                blueText = text
                                text.toIntOrNull()?.let { 
                                    if (it in 0..255) blueValue = it.toFloat()
                                }
                            },
                            modifier = Modifier.width(70.dp),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    blueText.toIntOrNull()?.let { 
                                        if (it in 0..255) blueValue = it.toFloat()
                                    }
                                }
                            ),
                            singleLine = true
                        )
                    }
                    Slider(
                        value = blueValue,
                        onValueChange = { blueValue = it },
                        valueRange = 0f..255f,
                        colors = SliderDefaults.colors(
                            thumbColor = androidx.compose.ui.graphics.Color.Blue,
                            activeTrackColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                onSave(selectedPresetColor, redValue, greenValue, blueValue)
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun FontSizeDialog(
    currentSize: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var fontSize by remember { mutableStateOf(currentSize) }
    var fontSizeText by remember { mutableStateOf(currentSize.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("字体大小设置") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 预览文本
                Text(
                    text = "预览文本",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 字体大小滑块
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("字体大小:", modifier = Modifier.width(80.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = fontSizeText,
                        onValueChange = { text ->
                            fontSizeText = text
                            text.toIntOrNull()?.let { 
                                if (it in 12..24) fontSize = it.toFloat()
                            }
                        },
                        modifier = Modifier.width(70.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                fontSizeText.toIntOrNull()?.let { 
                                    if (it in 12..24) fontSize = it.toFloat()
                                }
                            }
                        ),
                        singleLine = true
                    )
                }
                
                Slider(
                    value = fontSize,
                    onValueChange = { 
                        fontSize = it
                        fontSizeText = it.toInt().toString()
                    },
                    valueRange = 12f..24f,
                    steps = 12,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "范围: 12-24",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(fontSize) }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 日期选择器对话框 - 修复版
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
    initialDateTime: String
) {
    // 解析初始日期时间
    val initialDate = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.parse(initialDateTime) ?: Date()
    } catch (e: Exception) {
        Date()
    }
    
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    
    // 使用String类型而不是Int，允许空值和部分输入
    var yearText by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }
    var monthText by remember { mutableStateOf((calendar.get(Calendar.MONTH) + 1).toString()) }
    var dayText by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH).toString()) }
    var hourText by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY).toString()) }
    var minuteText by remember { mutableStateOf(calendar.get(Calendar.MINUTE).toString()) }
    var secondText by remember { mutableStateOf(calendar.get(Calendar.SECOND).toString()) }
    
    // 用于验证最终值
    fun validateDate(): Boolean {
        val year = yearText.toIntOrNull() ?: 0
        val month = monthText.toIntOrNull() ?: 0
        val day = dayText.toIntOrNull() ?: 0
        val hour = hourText.toIntOrNull() ?: 0
        val minute = minuteText.toIntOrNull() ?: 0
        val second = secondText.toIntOrNull() ?: 0
        
        return year in 1900..2100 && month in 1..12 && day in 1..31 &&
               hour in 0..23 && minute in 0..59 && second in 0..59
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期和时间") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 日期选择
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 年份选择
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it },
                        label = { Text("年") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        isError = yearText.isNotEmpty() && yearText.toIntOrNull() !in 1900..2100
                    )
                    
                    // 月份选择
                    OutlinedTextField(
                        value = monthText,
                        onValueChange = { monthText = it },
                        label = { Text("月") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = monthText.isNotEmpty() && monthText.toIntOrNull() !in 1..12
                    )
                    
                    // 日选择
                    OutlinedTextField(
                        value = dayText,
                        onValueChange = { dayText = it },
                        label = { Text("日") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = dayText.isNotEmpty() && dayText.toIntOrNull() !in 1..31
                    )
                }
                
                // 时间选择
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 小时选择
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { hourText = it },
                        label = { Text("时") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = hourText.isNotEmpty() && hourText.toIntOrNull() !in 0..23
                    )
                    
                    // 分钟选择
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it },
                        label = { Text("分") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = minuteText.isNotEmpty() && minuteText.toIntOrNull() !in 0..59
                    )
                    
                    // 秒选择
                    OutlinedTextField(
                        value = secondText,
                        onValueChange = { secondText = it },
                        label = { Text("秒") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = secondText.isNotEmpty() && secondText.toIntOrNull() !in 0..59
                    )
                }
                
                // 提示信息
                if (!validateDate()) {
                    Text(
                        text = "请输入有效的日期和时间",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 验证并格式化日期
                    try {
                        // 解析输入值，使用默认值替代无效值
                        val year = yearText.toIntOrNull() ?: calendar.get(Calendar.YEAR)
                        val monthValue = monthText.toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)
                        val month = monthValue - 1 // 转换为Calendar月份格式(0-11)
                        val day = dayText.toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
                        val hour = hourText.toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = minuteText.toIntOrNull() ?: calendar.get(Calendar.MINUTE)
                        val second = secondText.toIntOrNull() ?: calendar.get(Calendar.SECOND)
                        
                        // 创建新日历实例并设置时间
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, month)
                        
                        // 检查月份的最大天数
                        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val validDay = if (day > maxDay) maxDay else day
                        
                        // 设置完整日期时间
                        cal.set(year, month, validDay, hour, minute, second)
                        
                        // 格式化并返回
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDateTime = sdf.format(cal.time)
                        
                        onDateSelected(formattedDateTime)
                    } catch (e: Exception) {
                        // 发生异常时使用当前时间
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        onDateSelected(sdf.format(Date()))
                    }
                },
                enabled = validateDate()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 修改历史对话框
@Composable
fun EditHistoryDialog(
    noteId: Int,
    noteRepository: NoteRepository,
    onDismiss: () -> Unit
) {
    val historyList by noteRepository.getEditHistoryForNote(noteId).collectAsState(initial = emptyList())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改历史记录") },
        text = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                if (historyList.isEmpty()) {
                    Text(
                        text = "没有修改历史记录",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                } else {
                    // 带滚动的历史列表
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .fillMaxWidth()
                    ) {
                        items(historyList) { history ->
                            EditHistoryItem(history)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        },
        dismissButton = null
    )
}

// 修改历史项
@Composable
fun EditHistoryItem(history: EditHistoryEntity) {
    val typeText = when (history.editType) {
        "DATE_CREATED" -> "创建时间"
        "DATE_COMPLETED" -> "完成时间"
        "DATE_COMPLETED_REMOVED" -> "取消完成"
        else -> history.editType
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "修改类型: $typeText",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row {
                Text(
                    text = "修改时间: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                Text(
                    text = history.editTime,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (history.oldValue != null) {
                Row {
                    Text(
                        text = "旧值: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Text(
                        text = history.oldValue,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (history.newValue != null) {
                Row {
                    Text(
                        text = "新值: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Text(
                        text = history.newValue,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// 创建一个带滚动条和扩展按钮的文本输入框组件
@Composable
fun ScrollableOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    expanded: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(expanded) }
    var isFocused by remember { mutableStateOf(false) }
    
    // 当获得焦点时展开，失去焦点时如果expanded参数为false则收起
    // 增加内容框聚焦时的高度，从240dp增加到320dp
    val contentHeight = if (isFocused || isExpanded) 320.dp else 120.dp
    
    // 打印当前状态，帮助调试
    LaunchedEffect(isFocused, isExpanded) {
        println("ScrollableOutlinedTextField - 焦点状态: $isFocused, 扩展状态: $isExpanded, 高度设置为: $contentHeight")
    }
    
    Box(modifier = modifier.height(contentHeight)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    println("内容框焦点状态变化: $isFocused")
                }
                .verticalScroll(scrollState),
            textStyle = textStyle,
            interactionSource = remember { MutableInteractionSource() },
            singleLine = false,
            maxLines = if (isFocused || isExpanded) 50 else 5
        )
        
        // 始终显示滚动条
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp)
                .width(4.dp)
                .height(contentHeight - 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                )
        ) {
            // 计算滑块高度和位置
            val totalHeight = contentHeight - 20.dp
            // 滑块高度根据内容比例计算，但最小为20dp
            val thumbHeight = if (scrollState.maxValue > 0) {
                val viewportRatio = (contentHeight.value / (contentHeight.value + scrollState.maxValue))
                (totalHeight * viewportRatio).coerceAtLeast(20.dp)
            } else {
                totalHeight
            }
            
            // 滚动条滑块
            Box(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .width(4.dp)
                    .height(thumbHeight)
                    .offset(
                        y = if (scrollState.maxValue > 0) {
                            val availableScrollSpace = totalHeight - thumbHeight
                            availableScrollSpace * (scrollState.value.toFloat() / scrollState.maxValue.toFloat())
                        } else {
                            0.dp
                        }
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
            )
        }
        
        // 添加一个指示器，表明点击可扩展
        if (!isFocused && !isExpanded) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                    .clickable { 
                        isExpanded = true 
                        focusRequester.requestFocus()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "点击展开编辑区域",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Add the new AboutDialog composable function here
@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("关于 小纸条") },
        text = {
            Column {
                Text("小纸条 (Notes App)")
                Text("版本: 1.0.0") // You can make this dynamic if needed
                Spacer(modifier = Modifier.height(16.dp))
                Text("这是一个简单的笔记应用程序，用于记录您的想法和任务。")
                Spacer(modifier = Modifier.height(16.dp))
                Text("------红胡子", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashView(noteRepository: NoteRepository, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val deletedNotes by noteRepository.deletedNotes.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var showEmptyTrashConfirmDialog by remember { mutableStateOf(false) }

    // --- Empty Trash Confirmation Dialog ---
    if (showEmptyTrashConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashConfirmDialog = false },
            title = { Text("确认清空废纸篓") },
            text = { Text("确定要永久删除所有已删除的笔记吗？此操作不可撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            noteRepository.emptyTrash()
                        }
                        showEmptyTrashConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("全部删除")
                }
            },
            dismissButton = {
                Button(onClick = { showEmptyTrashConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("废纸篓") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEmptyTrashConfirmDialog = true },
                        enabled = deletedNotes.isNotEmpty()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "清空废纸篓")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (deletedNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("废纸篓是空的")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deletedNotes, key = { it.id }) { noteEntity ->
                    TrashNoteCard(
                        note = noteEntity,
                        onRestore = {
                            coroutineScope.launch {
                                noteRepository.restoreNoteById(noteEntity.id)
                            }
                        },
                        onDeletePermanently = {
                            coroutineScope.launch {
                                noteRepository.deleteNotesByIdsPermanently(listOf(noteEntity.id))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrashNoteCard(note: NoteEntity, onRestore: () -> Unit, onDeletePermanently: () -> Unit) {
    var showDeletePermanentConfirmDialog by remember { mutableStateOf(false) }

    if (showDeletePermanentConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePermanentConfirmDialog = false },
            title = { Text("确认永久删除") },
            text = { Text("此操作不可撤销，确定要永久删除这个笔记吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePermanently()
                        showDeletePermanentConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("永久删除")
                }
            },
            dismissButton = {
                Button(onClick = { showDeletePermanentConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(note.content, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { showDeletePermanentConfirmDialog = true }) {
                    Text("永久删除", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRestore) {
                    Text("恢复")
                }
            }
        }
    }
}