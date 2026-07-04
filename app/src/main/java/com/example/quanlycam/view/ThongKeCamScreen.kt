package com.example.quanlycam.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quanlycam.ui.theme.ActiveBlue
import com.example.quanlycam.ui.theme.BgColor
import com.example.quanlycam.ui.theme.DarkGreen
import com.example.quanlycam.ui.theme.GrayBorder
import com.example.quanlycam.ui.theme.GrayText
import com.example.quanlycam.ui.theme.TabUnselectedBg
import com.example.quanlycam.ui.theme.QuanLyCamTheme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThongKeCamScreen(
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quản lý nhập cám",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("danhsach") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DarkGreen)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = GrayBorder,
                    shape = RoundedCornerShape(0.dp)
                )            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ", fontSize = 11.sp) },
                    selected = false,
                    onClick = { onNavigate("danhmuc") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Danh sách", fontSize = 11.sp) },
                    selected = false,
                    onClick = { onNavigate("danhsach") }
                )
                NavigationBarItem(
                    icon = {
                        // Sử dụng icon giả lập Thống kê
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ActiveBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, tint = ActiveBlue)
                        }
                    },
                    label = { Text("Thống kê", fontSize = 11.sp, color = ActiveBlue, fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Cá nhân", fontSize = 11.sp) },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { innerPadding ->
        var selectedPeriod by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // --- 1. Bộ lọc Thời gian (Tháng, Quý, Năm) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TabUnselectedBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Tháng", "Quý", "Năm").forEachIndexed { index, period ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedPeriod == index) DarkGreen else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedPeriod == index) Color.White else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // --- 2. Thẻ: Thống kê đổi cám ---
            ThongKeCard {
                Text("Thống kê đổi cám", fontSize = 13.sp, color = Color.Black)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("12", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Lần đổi", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = GrayText)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ổn định so với kỳ trước", fontSize = 12.sp, color = GrayText)
                }
            }

            // --- 4. Thẻ: Tổng số lượng nhập (Biểu đồ cột) ---
            ThongKeCard {
                Text("Tổng số lượng nhập", fontSize = 13.sp, color = Color.Black)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("1,808", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("bao", fontSize = 13.sp, color = GrayText, modifier = Modifier.padding(bottom = 4.dp))
                }

                // Biểu đồ cột giả lập
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(0.3f, 0.5f, 1f, 0.4f, 0.3f, 0.6f, 0.2f)
                    heights.forEachIndexed { idx, weight ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(weight)
                                .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                .background(if (idx == 2) DarkGreen else Color(0xFFE5E7EB))
                        )
                    }
                }
            }

            // --- 5. Thẻ: Phân bổ loại cám ---
            ThongKeCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phân bổ loại cám", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Icon(Icons.Default.List, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Dòng 1
                ProgressBarRow(label = "Cám khởi động (Stage 1)", value = "500 bao", progress = 0.4f)
                // Dòng 2
                ProgressBarRow(label = "Cám tăng trưởng (Stage 2)", value = "880 bao", progress = 0.7f)
                // Dòng 3
                ProgressBarRow(label = "Cám vỗ béo (Stage 3)", value = "428 bao", progress = 0.35f)
            }

            // --- 6. Thẻ: Biến động số lượng bao theo thời gian ---
            ThongKeCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Biến động số lượng bao theo thời gian", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Biểu đồ đường vẽ bằng Canvas giả lập theo hình vẽ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Vẽ các đường lưới ngang nét đứt
                        val lines = 3
                        for (i in 1..lines) {
                            val y = (height / (lines + 1)) * i
                            drawLine(
                                color = Color(0xFFE5E7EB),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 2f
                            )
                        }

                        // Tạo path đường cong nối các tuần
                        val p1 = Offset(width * 0.05f, height * 0.75f)
                        val p2 = Offset(width * 0.3f, height * 0.55f)
                        val p3 = Offset(width * 0.55f, height * 0.25f)
                        val p4 = Offset(width * 0.78f, height * 0.68f)
                        val p5 = Offset(width * 0.95f, height * 0.2f)

                        val strokePath = Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p3.x, p3.y)
                            lineTo(p4.x, p4.y)
                            lineTo(p5.x, p5.y)
                        }

                        // Đổ màu vùng dưới đồ thị gradient nhạt dần
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(p5.x, height)
                            lineTo(p1.x, height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(DarkGreen.copy(alpha = 0.15f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Vẽ đường Line chính màu xanh lục
                        drawPath(
                            path = strokePath,
                            color = DarkGreen,
                            style = Stroke(width = 4f)
                        )

                        // Vẽ các điểm nút hình tròn nhỏ viền trắng nền xanh
                        listOf(p1, p2, p3, p4).forEach { pt ->
                            drawCircle(color = Color.White, radius = 8f, center = pt)
                            drawCircle(color = DarkGreen, radius = 5f, center = pt)
                        }
                    }
                }

                // Hàng text nhãn Tuần dưới biểu đồ
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4").forEach { text ->
                        Text(text, fontSize = 11.sp, color = GrayText)
                    }
                }
            }

            // --- 7. Nút Xuất báo cáo chi tiết (.xlsx) ---
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Xuất báo cáo chi tiết (.xlsx)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- THÀNH PHẦN CON: THẺ THỐNG KÊ CONTAINER ---
@Composable
fun ThongKeCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GrayBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// --- THÀNH PHẦN CON: THANH TIẾN TRÌNH THEO LOẠI CÁM ---
@Composable
fun ProgressBarRow(label: String, value: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = Color.Black)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = DarkGreen,
            trackColor = TabUnselectedBg,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// --- HÀM PREVIEW GIAO DIỆN ---
@Preview(showBackground = true)
@Composable
fun ThongKeCamScreenPreview() {
    QuanLyCamTheme {
        ThongKeCamScreen()
    }
}