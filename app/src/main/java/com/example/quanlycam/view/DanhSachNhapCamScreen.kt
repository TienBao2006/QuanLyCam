package com.example.quanlycam.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.ui.theme.GrayBorder
import com.example.quanlycam.ui.theme.GrayText
import com.example.quanlycam.ui.theme.QuanLyCamTheme
import com.example.quanlycam.viewmodel.DanhSachNhapCamViewModel
import com.example.quanlycam.viewmodel.QuanLyNhapCamViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val DsOrangeText = Color(0xFFC25745)
private val DsMintBg     = Color(0xFFD4EAE2)
private val DsCardBg     = Color(0xFFFDFBF7)
private val DsGrayIcon   = Color(0xFF8E8E93)
private val dsFmt        = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanhSachNhapCamScreen(
    onNavigate: (String) -> Unit = {},
    vm: DanhSachNhapCamViewModel = viewModel(),
    vmNhap: QuanLyNhapCamViewModel = viewModel()
) {
    val danhSach by vm.danhSach.collectAsStateWithLifecycle()
    val loi by vm.loi.collectAsStateWithLifecycle()

    var tuKhoa        by remember { mutableStateOf("") }
    var tuNgay        by remember { mutableStateOf<Long?>(null) }
    var denNgay       by remember { mutableStateOf<Long?>(null) }
    var loaiLoc       by remember { mutableStateOf("") }
    var showPickerTu  by remember { mutableStateOf(false) }
    var showPickerDen by remember { mutableStateOf(false) }
    var showLoaiMenu  by remember { mutableStateOf(false) }
    val datePickerTu  = rememberDatePickerState()
    val datePickerDen = rememberDatePickerState()

    val danhSachLoai = remember(danhSach) {
        danhSach.map { it.tenLoaiCam }.filter { it.isNotBlank() }.distinct().sorted()
    }

    val danhSachLoc = remember(danhSach, tuKhoa, tuNgay, denNgay, loaiLoc) {
        danhSach.filter { p ->
            val matchText = tuKhoa.isBlank() ||
                p.tenLoaiCam.contains(tuKhoa, ignoreCase = true) ||
                p.maLoaiCam.contains(tuKhoa, ignoreCase = true)
            val ms = runCatching { dsFmt.parse(p.ngayNhap)?.time }.getOrNull()
            val matchTu   = tuNgay  == null || (ms != null && ms >= tuNgay!!)
            val matchDen  = denNgay == null || (ms != null && ms <= denNgay!! + 86_400_000L)
            val matchLoai = loaiLoc.isBlank() || p.tenLoaiCam == loaiLoc
            matchText && matchTu && matchDen && matchLoai
        }
    }

    val now = remember { Calendar.getInstance() }
    val tongBaoThang = remember(danhSach) {
        danhSach.filter { p ->
            val d = runCatching { dsFmt.parse(p.ngayNhap) }.getOrNull() ?: return@filter false
            val c = Calendar.getInstance().apply { time = d }
            c.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }.sumOf { it.soLuong }
    }
    val tenThang = "Thang ${now.get(Calendar.MONTH) + 1}/${now.get(Calendar.YEAR)}"

    var phieuCanXoa by remember { mutableStateOf<PhieuNhapCam?>(null) }

    if (showPickerTu) {
        DatePickerDialog(
            onDismissRequest = { showPickerTu = false },
            confirmButton = { TextButton(onClick = { tuNgay = datePickerTu.selectedDateMillis; showPickerTu = false }) { Text("Chon") } },
            dismissButton  = { TextButton(onClick = { showPickerTu = false }) { Text("Huy") } }
        ) { DatePicker(state = datePickerTu) }
    }
    if (showPickerDen) {
        DatePickerDialog(
            onDismissRequest = { showPickerDen = false },
            confirmButton = { TextButton(onClick = { denNgay = datePickerDen.selectedDateMillis; showPickerDen = false }) { Text("Chon") } },
            dismissButton  = { TextButton(onClick = { showPickerDen = false }) { Text("Huy") } }
        ) { DatePicker(state = datePickerDen) }
    }
    if (phieuCanXoa != null) {
        AlertDialog(
            onDismissRequest = { phieuCanXoa = null },
            title   = { Text("Xac nhan xoa") },
            text    = { Text("Xoa phieu \"${phieuCanXoa?.tenLoaiCam}\"?") },
            confirmButton = { TextButton(onClick = { vm.xoa(phieuCanXoa!!.id); phieuCanXoa = null }) { Text("Xoa", color = DsOrangeText) } },
            dismissButton = { TextButton(onClick = { phieuCanXoa = null }) { Text("Huy") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vmNhap.resetTrangThai(); onNavigate("nhap") },
                containerColor = DsOrangeText,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Them moi", modifier = Modifier.size(28.dp))
            }
        },
        bottomBar = {
            AppBottomBar(current = BottomTab.DANH_SACH, onNavigate = onNavigate)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4EDE4))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (loi != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDED)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DsOrangeText, modifier = Modifier.size(18.dp))
                            Text(loi ?: "", color = DsOrangeText, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = tuKhoa,
                    onValueChange = { tuKhoa = it },
                    placeholder = { Text("Tim kiem loai cam...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (tuKhoa.isNotBlank()) {
                            IconButton(onClick = { tuKhoa = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = GrayBorder,
                        unfocusedBorderColor = GrayBorder
                    )
                )
            }

            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tu ngay", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedButton(
                                onClick = { showPickerTu = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GrayBorder),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (tuNgay == null) GrayText else Color.Black)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (tuNgay == null) "Chon" else dsFmt.format(Date(tuNgay!!)), fontSize = 11.sp, maxLines = 1)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Den ngay", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedButton(
                                onClick = { showPickerDen = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GrayBorder),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (denNgay == null) GrayText else Color.Black)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (denNgay == null) "Chon" else dsFmt.format(Date(denNgay!!)), fontSize = 11.sp, maxLines = 1)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Loai cam", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                            Box {
                                OutlinedButton(
                                    onClick = { showLoaiMenu = true },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (loaiLoc.isBlank()) GrayText else Color.Black)
                                ) {
                                    Text(if (loaiLoc.isBlank()) "Tat ca" else loaiLoc.take(7), fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = showLoaiMenu, onDismissRequest = { showLoaiMenu = false }) {
                                    DropdownMenuItem(text = { Text("Tat ca") }, onClick = { loaiLoc = ""; showLoaiMenu = false })
                                    danhSachLoai.forEach { ten ->
                                        DropdownMenuItem(text = { Text(ten, fontSize = 13.sp) }, onClick = { loaiLoc = ten; showLoaiMenu = false })
                                    }
                                }
                            }
                        }
                    }
                    if (tuNgay != null || denNgay != null || loaiLoc.isNotBlank()) {
                        TextButton(onClick = { tuNgay = null; denNgay = null; loaiLoc = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Xoa bo loc", fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DsMintBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TONG NHAP $tenThang", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E5B4A))
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(dsFmtBao(tongBaoThang), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DsOrangeText)
                            Text("bao", fontSize = 14.sp, color = Color(0xFF2E5B4A), modifier = Modifier.padding(bottom = 3.dp))
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Danh sach (${danhSachLoc.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF262626))
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEAE3D8)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xuat bao cao", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }

            if (danhSachLoc.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            if (tuKhoa.isBlank() && tuNgay == null && denNgay == null && loaiLoc.isBlank())
                                "Chua co phieu nhap nao. Nhan + de them moi."
                            else "Khong tim thay phieu phu hop.",
                            color = GrayText, fontSize = 14.sp, textAlign = TextAlign.Center
                        )
                    }
                }
            }

            items(danhSachLoc, key = { it.id }) { phieu ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE5DDD2), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DsCardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                phieu.maPhieu.ifBlank { phieu.id.take(14).uppercase() },
                                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray
                            )
                            Row {
                                IconButton(onClick = { vmNhap.batDauSua(phieu); onNavigate("nhap") }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Sua", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { phieuCanXoa = phieu }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xoa", tint = DsOrangeText, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(22.dp).border(1.dp, DsGrayIcon, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(14.dp), tint = DsGrayIcon)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(phieu.tenLoaiCam, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = GrayBorder.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = DsGrayIcon)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(phieu.ngayNhap, fontSize = 13.sp, color = Color.DarkGray)
                            }
                            Text("${dsFmtBao(phieu.soLuong)} bao", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DsOrangeText)
                        }
                        if (phieu.ghiChu.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Ghi chu: ${phieu.ghiChu}", fontSize = 12.sp, color = GrayText, maxLines = 1)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

private fun dsFmtBao(value: Int): String =
    String.format(Locale.getDefault(), "%,d", value).replace(',', '.')

@Preview(showBackground = true)
@Composable
fun DanhSachNhapCamScreenPreview() {
    QuanLyCamTheme {
        DanhSachNhapCamScreen()
    }
}