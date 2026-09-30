package com.xiaozhitiao.app.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * 日期时间选择器对话框，用于设置提醒时间
 */
@Composable
fun ReminderDateTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val currentDateTime = Calendar.getInstance()
    
    // 初始化年、月、日、时、分的状态
    var selectedYear by remember { mutableStateOf(currentDateTime.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(currentDateTime.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(currentDateTime.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(currentDateTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(currentDateTime.get(Calendar.MINUTE)) }
    
    // 获取当前月份的最大天数
    val maxDays = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    
    // 创建滚动状态，初始位置设置为能让选中项居中显示
    val yearScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, selectedYear - 2023)
    )
    val monthScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, selectedMonth)
    )
    val dayScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, selectedDay - 1)
    )
    val hourScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, selectedHour)
    )
    val minuteScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, selectedMinute)
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    // 监听滚动状态变化，确保选中项在中间
    LaunchedEffect(yearScrollState.isScrollInProgress) {
        if (!yearScrollState.isScrollInProgress) {
            val centerIndex = yearScrollState.firstVisibleItemIndex
            if (centerIndex in 0..9) {
                selectedYear = centerIndex + 2023
            }
            coroutineScope.launch {
                yearScrollState.animateScrollToItem(selectedYear - 2023)
            }
        }
    }
    
    LaunchedEffect(monthScrollState.isScrollInProgress) {
        if (!monthScrollState.isScrollInProgress) {
            val centerIndex = monthScrollState.firstVisibleItemIndex
            if (centerIndex in 0..11) {
                selectedMonth = centerIndex
            }
            coroutineScope.launch {
                monthScrollState.animateScrollToItem(selectedMonth)
            }
        }
    }
    
    LaunchedEffect(dayScrollState.isScrollInProgress) {
        if (!dayScrollState.isScrollInProgress) {
            val centerIndex = dayScrollState.firstVisibleItemIndex
            if (centerIndex in 0 until maxDays) {
                selectedDay = centerIndex + 1
            }
            coroutineScope.launch {
                dayScrollState.animateScrollToItem(selectedDay - 1)
            }
        }
    }
    
    LaunchedEffect(hourScrollState.isScrollInProgress) {
        if (!hourScrollState.isScrollInProgress) {
            val centerIndex = hourScrollState.firstVisibleItemIndex
            if (centerIndex in 0..23) {
                selectedHour = centerIndex
            }
            coroutineScope.launch {
                hourScrollState.animateScrollToItem(selectedHour)
            }
        }
    }
    
    LaunchedEffect(minuteScrollState.isScrollInProgress) {
        if (!minuteScrollState.isScrollInProgress) {
            val centerIndex = minuteScrollState.firstVisibleItemIndex
            if (centerIndex in 0..59) {
                selectedMinute = centerIndex
            }
            coroutineScope.launch {
                minuteScrollState.animateScrollToItem(selectedMinute)
            }
        }
    }
    
    // 确保选择的日期有效
    LaunchedEffect(maxDays) {
        if (selectedDay > maxDays) {
            selectedDay = maxDays
            coroutineScope.launch {
                dayScrollState.animateScrollToItem(selectedDay - 1)
            }
        }
    }
    
    // 格式化月份名称
    val monthNames = listOf("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")
    
    // 当组件首次加载时，确保所有选择器都滚动到正确的初始位置
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            yearScrollState.scrollToItem(maxOf(0, selectedYear - 2023))
            monthScrollState.scrollToItem(maxOf(0, selectedMonth))
            dayScrollState.scrollToItem(maxOf(0, selectedDay - 1))
            hourScrollState.scrollToItem(maxOf(0, selectedHour))
            minuteScrollState.scrollToItem(maxOf(0, selectedMinute))
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "设置提醒时间",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 日期选择器
                Text(
                    text = "日期",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 年份选择器
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = yearScrollState,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = yearScrollState)
                        ) {
                            // 添加上方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                            
                            items(10) { index ->
                                val year = 2023 + index
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = year.toString(),
                                        fontSize = if (year == selectedYear) 18.sp else 14.sp,
                                        fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                                        color = if (year == selectedYear) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // 添加下方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                        
                        // 选择指示器
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    
                    // 月份选择器
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = monthScrollState,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = monthScrollState)
                        ) {
                            // 添加上方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                            
                            items(12) { index ->
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = monthNames[index],
                                        fontSize = if (index == selectedMonth) 18.sp else 14.sp,
                                        fontWeight = if (index == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                                        color = if (index == selectedMonth) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // 添加下方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                        
                        // 选择指示器
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    
                    // 日选择器
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = dayScrollState,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = dayScrollState)
                        ) {
                            // 添加上方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                            
                            items(maxDays) { index ->
                                val day = index + 1
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${day}日",
                                        fontSize = if (day == selectedDay) 18.sp else 14.sp,
                                        fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal,
                                        color = if (day == selectedDay) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // 添加下方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                        
                        // 选择指示器
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时间选择器
                Text(
                    text = "时间",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 小时选择器
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = hourScrollState,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = hourScrollState)
                        ) {
                            // 添加上方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                            
                            items(24) { hour ->
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = String.format("%02d", hour),
                                        fontSize = if (hour == selectedHour) 18.sp else 14.sp,
                                        fontWeight = if (hour == selectedHour) FontWeight.Bold else FontWeight.Normal,
                                        color = if (hour == selectedHour) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // 添加下方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                        
                        // 选择指示器
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    // 分钟选择器
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = minuteScrollState,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = minuteScrollState)
                        ) {
                            // 添加上方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                            
                            items(60) { minute ->
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = String.format("%02d", minute),
                                        fontSize = if (minute == selectedMinute) 18.sp else 14.sp,
                                        fontWeight = if (minute == selectedMinute) FontWeight.Bold else FontWeight.Normal,
                                        color = if (minute == selectedMinute) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            // 添加下方空白项
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                        
                        // 选择指示器
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 显示选择的日期和时间
                Text(
                    text = "${selectedYear}年${selectedMonth + 1}月${selectedDay}日 ${String.format("%02d:%02d", selectedHour, selectedMinute)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // 操作按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            // 合并日期和时间
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, selectedMonth)
                                set(Calendar.DAY_OF_MONTH, selectedDay)
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            
                            // 确保选择的时间在当前时间之后
                            val now = Calendar.getInstance()
                            if (calendar.before(now)) {
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                            
                            onConfirm(calendar.timeInMillis)
                        }
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
} 