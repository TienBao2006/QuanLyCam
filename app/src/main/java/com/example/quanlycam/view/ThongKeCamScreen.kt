package com.example.quanlycam.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlycam.ui.theme.*
import com.example.quanlycam.viewmodel.ThongKeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThongKeCamScreen(
    onNavigate: (String) -> Unit = {},
    vm: ThongKeViewModel = viewModel()
) {
    val kyChon by vm.kyChon.collectAsStateWithLifecycle()
    val tk by vm.thongKe.collectAsStateWithLifecycle()
    val loi by vm.loi.collectAsStateWithLifecycle()

    val now = remember { Calendar.getInstance() }
    val tenKy = when (kyChon) {
        0 -> "Tháng ${now.get(Calendar.MONTH) + 1}/${now.get(Calendar.YEAR)}"
        1 -> "Quý ${now.get(Calendar.MONTH) / 3 + 1}/${now.get(Calendar.YEAR)}"
        2 -> "Năm ${now.get(Calendar.YEAR)}"
        else -> ""
    }
    val maxBienDong = remember(tk.bienDongTuan) { tk.bienDongTuan.maxOrNull()?.takeIf { it > 0 } ?: 1 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê nhập cám", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen) },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("danhsach") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DarkGreen)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp).size(36.dp).clip(CircleShape).background(Color.LightGray))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            AppBottomBar(current = BottomTab.THONG_KE, onNavigate = onNavigate)
        }
    ) { innerPadding ->
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

            if (loi != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDED)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(loi ?: "", color = Color(0xFFC25745), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            // Bộ lọc kỳ
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(TabUnselectedBg).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Tháng", "Quý", "Năm").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier.weight(1f).height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (kyChon == index) DarkGreen else Color.Transparent)
                            .clickable { vm.chonKy(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontWeight = FontWeight.Bold, color = if (kyChon == index) Color.White else Color.Black, fontSize = 14.sp)
                    }
                }
            }

            // Tổng quan
            ThongKeCard {
                Text(tenKy, fontSize = 12.sp, color = GrayText)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Tổng số lượng nhập", fontSize = 13.sp, color = Color.DarkGray)
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(formatSoLuong(tk.tongSoLuong), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                            Text("bao", fontSize = 13.sp, color = GrayText, modifier = Modifier.padding(bottom = 5.dp))
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Số phiếu", fontSize = 13.sp, color = Color.DarkGray)
                        Text("${tk.tongPhieu} phiếu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val diff = tk.tongSoLuong - tk.soLuongKyTruoc
                val diffText = when {
                    tk.soLuongKyTruoc == 0 -> "Chưa có dữ liệu kỳ trước"
                    diff > 0 -> "▲ Tăng ${formatSoLuong(diff)} bao so với kỳ trước"
                    diff < 0 -> "▼ Giảm ${formatSoLuong(-diff)} bao so với kỳ trước"
                    else -> "Ổn định so với kỳ trước"
                }
                Text(diffText, fontSize = 12.sp, color = when {
                    diff > 0 -> Color(0xFF2E7D32); diff < 0 -> Color(0xFFC62828); else -> GrayText
                })
            }

            // Biểu đồ cột tuần (chỉ hiện khi xem tháng)
            if (kyChon == 0) {
                ThongKeCard {
                    Text("Biến động theo tuần — $tenKy", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (tk.bienDongTuan.all { it == 0 }) {
                        Text("Không có dữ liệu trong tháng này", fontSize = 13.sp, color = GrayText, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                            tk.bienDongTuan.forEach { value ->
                                val ratio = value.toFloat() / maxBienDong
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                    if (value > 0) Text(formatSoLuong(value), fontSize = 9.sp, color = DarkGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(if (ratio > 0f) ratio else 0.04f).clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)).background(if (value == maxBienDong) DarkGreen else Color(0xFFB0D4C4)))
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4").forEach { t ->
                                Text(t, fontSize = 11.sp, color = GrayText, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            // Biểu đồ đường
            ThongKeCard {
                Text("Biến động số lượng bao theo thời gian", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                val points = tk.bienDongTuan
                val maxVal = points.maxOrNull()?.takeIf { it > 0 } ?: 1
                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width; val h = size.height; val n = points.size
                        repeat(3) { i -> drawLine(Color(0xFFE5E7EB), Offset(0f, h / 4f * (i + 1)), Offset(w, h / 4f * (i + 1)), strokeWidth = 1.5f) }
                        if (n >= 2 && points.any { it > 0 }) {
                            val pts = points.mapIndexed { i, v -> Offset(w * i / (n - 1).toFloat(), h - (h * v.toFloat() / maxVal) * 0.85f - h * 0.05f) }
                            val linePath = Path().apply { moveTo(pts[0].x, pts[0].y); pts.drop(1).forEach { lineTo(it.x, it.y) } }
                            val fillPath = Path().apply { addPath(linePath); lineTo(pts.last().x, h); lineTo(pts.first().x, h); close() }
                            drawPath(fillPath, brush = Brush.verticalGradient(listOf(DarkGreen.copy(alpha = 0.18f), Color.Transparent), 0f, h))
                            drawPath(linePath, color = DarkGreen, style = Stroke(width = 3.5f))
                            pts.forEach { pt -> drawCircle(Color.White, radius = 7f, center = pt); drawCircle(DarkGreen, radius = 4.5f, center = pt) }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4").forEach { t ->
                        Text(t, fontSize = 11.sp, color = GrayText, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
            }

            // Phân bổ loại cám
            ThongKeCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Phân bổ loại cám", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(tenKy, fontSize = 12.sp, color = GrayText)
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (tk.phanBoLoai.isEmpty()) {
                    Text("Không có dữ liệu trong kỳ này", fontSize = 13.sp, color = GrayText)
                } else {
                    val tongPhanBo = tk.phanBoLoai.sumOf { it.second }.takeIf { it > 0 } ?: 1
                    tk.phanBoLoai.take(6).forEach { (ten, soLuong) ->
                        ProgressBarRow(label = ten.ifBlank { "(Không tên)" }, value = "${formatSoLuong(soLuong)} bao", progress = soLuong.toFloat() / tongPhanBo)
                    }
                }
            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xuất báo cáo chi tiết", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatSoLuong(value: Int): String =
    String.format("%,d", value).replace(',', '.')

@Composable
fun ThongKeCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, GrayBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}

@Composable
fun ProgressBarRow(label: String, value: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, color = Color.Black, modifier = Modifier.weight(1f))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = DarkGreen,
            trackColor = TabUnselectedBg
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ThongKeCamScreenPreview() {
    QuanLyCamTheme {
        ThongKeCamScreen()
    }
}
