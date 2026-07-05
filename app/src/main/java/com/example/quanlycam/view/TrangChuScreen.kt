package com.example.quanlycam.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.viewmodel.DanhSachNhapCamViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val TcGreen      = Color(0xFF2B5E38)
private val TcGreenLight = Color(0xFFD6EAD8)
private val TcBg         = Color(0xFFF8F6F2)
private val TcCardBg     = Color(0xFFFFFFFF)
private val TcGray       = Color(0xFF6B7280)
private val TcBarInactive = Color(0xFFB8D9BF)
private val dsFmtTc      = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@Composable
fun TrangChuScreen(
    onNavigate: (String) -> Unit = {},
    vm: DanhSachNhapCamViewModel = viewModel()
) {
    val danhSach by vm.danhSach.collectAsStateWithLifecycle()
    val now = remember { Calendar.getInstance() }

    // Tổng nhập hôm nay
    val tongHomNay = remember(danhSach) {
        val today = Calendar.getInstance()
        danhSach.filter { p ->
            val c = parseCalTc(p.ngayNhap) ?: return@filter false
            c.get(Calendar.YEAR)         == today.get(Calendar.YEAR) &&
            c.get(Calendar.DAY_OF_YEAR)  == today.get(Calendar.DAY_OF_YEAR)
        }.sumOf { it.soLuong }
    }

    // Số chuyến hôm nay (số phiếu)
    val soChuyenHomNay = remember(danhSach) {
        val today = Calendar.getInstance()
        danhSach.count { p ->
            val c = parseCalTc(p.ngayNhap) ?: return@count false
            c.get(Calendar.YEAR)        == today.get(Calendar.YEAR) &&
            c.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }
    }

    // Dữ liệu 7 ngày gần nhất cho biểu đồ
    val data7Ngay = remember(danhSach) {
        (6 downTo 0).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val tong = danhSach.filter { p ->
                val c = parseCalTc(p.ngayNhap) ?: return@filter false
                c.get(Calendar.YEAR)        == cal.get(Calendar.YEAR) &&
                c.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
            }.sumOf { it.soLuong }
            val thu = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY    -> "T2"
                Calendar.TUESDAY   -> "T3"
                Calendar.WEDNESDAY -> "T4"
                Calendar.THURSDAY  -> "T5"
                Calendar.FRIDAY    -> "T6"
                Calendar.SATURDAY  -> "T7"
                else               -> "CN"
            }
            Pair(thu, tong)
        }
    }

    val trungBinh7Ngay = remember(data7Ngay) {
        val sum = data7Ngay.sumOf { it.second }
        if (sum > 0) sum / 7 else 0
    }

    // 3 giao dịch gần nhất
    val giaoDichGanDay = remember(danhSach) {
        danhSach.sortedByDescending { p ->
            runCatching { dsFmtTc.parse(p.ngayNhap)?.time }.getOrNull() ?: 0L
        }.take(3)
    }

    val thuStr = remember {
        val ngay = listOf("Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy")
        ngay[now.get(Calendar.DAY_OF_WEEK) - 1].uppercase()
    }
    val ngayStr = remember {
        "${thuStr}, ${now.get(Calendar.DAY_OF_MONTH)} THÁNG ${now.get(Calendar.MONTH) + 1} ${now.get(Calendar.YEAR)}"
    }
    val buoiChao = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11  -> "Chào buổi sáng,"
            in 12..17 -> "Chào buổi chiều,"
            else      -> "Chào buổi tối,"
        }
    }

    Scaffold(
        containerColor = TcBg,
        bottomBar = { AppBottomBar(current = BottomTab.TRANG_CHU, onNavigate = onNavigate) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TcBg)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(TcGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Grass, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Text("AgriFlow", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TcGreen)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = TcGray, modifier = Modifier.size(24.dp))
                    Box(
                        modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFD6D3CC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("mg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }

            // ── Lời chào ─────────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(buoiChao, fontSize = 13.sp, color = TcGray)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nguyễn Văn Ninh", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Text("👋", fontSize = 20.sp)
                }
                Text(ngayStr, fontSize = 12.sp, color = TcGray, fontWeight = FontWeight.Medium)
            }

            // ── Tổng nhập hôm nay ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TcCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tổng nhập hôm nay", fontSize = 13.sp, color = TcGray)
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                fmtSoBao(tongHomNay),
                                fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)
                            )
                            Text("bao", fontSize = 15.sp, color = TcGray, modifier = Modifier.padding(bottom = 5.dp))
                        }
                        Text(
                            if (tongHomNay > 0) "↑ Tăng nhẹ so với trung bình tuần trước"
                            else "* Chưa có dữ liệu hôm nay",
                            fontSize = 11.sp, color = if (tongHomNay > 0) Color(0xFF2E7D32) else TcGray
                        )
                    }
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(TcGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TcGreen, modifier = Modifier.size(26.dp))
                    }
                }
            }

            // ── Số chuyến ────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TcCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(TcGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TcGreen, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("SỐ CHUYẾN", fontSize = 11.sp, color = TcGray, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
                        Text(
                            String.format("%02d", soChuyenHomNay),
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)
                        )
                    }
                }
            }

            // ── Thao tác nhanh ───────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("THAO TÁC NHANH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), letterSpacing = 0.5.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onNavigate("nhap") },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TcGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nhập cám mới", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { onNavigate("thongke") },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TcGreen),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, TcGreen)
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xem báo cáo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Biểu đồ 7 ngày ───────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TcCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("XU HƯỚNG 7 NGÀY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), letterSpacing = 0.5.sp)
                        Text("Đơn vị:\nbao", fontSize = 10.sp, color = TcGray, lineHeight = 13.sp)
                    }
                    BarChart7Ngay(data = data7Ngay)
                    Text(
                        "Trung bình: ${fmtSoBao(trungBinh7Ngay)} bao/ngày",
                        fontSize = 12.sp, color = TcGreen, fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── Giao dịch gần đây ────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GIAO DỊCH GẦN ĐÂY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), letterSpacing = 0.5.sp)
                    Text(
                        "XEM TẤT CẢ",
                        fontSize = 11.sp, color = TcGreen, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate("danhsach") }
                    )
                }
                if (giaoDichGanDay.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TcCardBg)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có giao dịch nào", fontSize = 13.sp, color = TcGray)
                        }
                    }
                } else {
                    giaoDichGanDay.forEach { phieu ->
                        GiaoDichItem(phieu = phieu)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BarChart7Ngay(data: List<Pair<String, Int>>) {
    val maxVal = data.maxOfOrNull { it.second }.takeIf { it != null && it > 0 } ?: 1
    val todayIdx = data.lastIndex

    Row(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { idx, (thu, value) ->
            val ratio = value.toFloat() / maxVal
            val isToday = idx == todayIdx
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (value > 0) {
                    Text(
                        fmtSoBao(value),
                        fontSize = 8.sp,
                        color = if (isToday) TcGreen else TcGray,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (ratio > 0f) ratio.coerceAtLeast(0.05f) else 0.05f)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (isToday) TcGreen else TcBarInactive)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(thu, fontSize = 10.sp, color = if (isToday) TcGreen else TcGray, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun GiaoDichItem(phieu: PhieuNhapCam) {
    val icons = listOf(
        Icons.Default.Water,
        Icons.Default.Pets,
        Icons.Default.Agriculture
    )
    val iconColors = listOf(
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFF4CAF50)
    )
    val iconIdx = (phieu.tenLoaiCam.length + phieu.maLoaiCam.length) % 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TcCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconColors[iconIdx].copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icons[iconIdx], contentDescription = null, tint = iconColors[iconIdx], modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    phieu.tenLoaiCam.ifBlank { "Không tên" },
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    buildString {
                        append(phieu.ngayNhap)
                        if (phieu.maPhieu.isNotBlank()) append("  •  ${phieu.maPhieu}")
                    },
                    fontSize = 11.sp, color = TcGray
                )
            }
            Text(
                "${fmtSoBao(phieu.soLuong)} bao",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TcGreen
            )
        }
    }
}

private fun parseCalTc(ngay: String): Calendar? {
    val date = runCatching { dsFmtTc.parse(ngay) }.getOrNull() ?: return null
    return Calendar.getInstance().apply { time = date }
}

private fun fmtSoBao(value: Int): String =
    String.format(Locale.getDefault(), "%,d", value).replace(',', '.')
