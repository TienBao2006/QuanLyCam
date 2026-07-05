package com.example.quanlycam.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlycam.ui.theme.QuanLyCamTheme
import com.example.quanlycam.viewmodel.DanhMucLoaiCamViewModel
import com.example.quanlycam.viewmodel.QuanLyNhapCamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val QlDarkGreen = Color(0xFF114D35)
private val QlGrayBorder = Color(0xFFD6D6D6)
private val QlGrayText = Color(0xFF777777)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuanLyNhapCamScreen(
    onNavigate: (String) -> Unit = {},
    vm: QuanLyNhapCamViewModel = viewModel(),
    vmLoai: DanhMucLoaiCamViewModel = viewModel()
) {
    val danhSachLoai by vmLoai.danhSach.collectAsStateWithLifecycle()
    val dangLuu by vm.dangLuu.collectAsStateWithLifecycle()
    val luuThanhCong by vm.luuThanhCong.collectAsStateWithLifecycle()
    val loi by vm.loi.collectAsStateWithLifecycle()
    val phieuDangSua by vm.phieuDangSua.collectAsStateWithLifecycle()

    // Khởi tạo form với dữ liệu phiếu đang sửa (nếu có), chạy đúng 1 lần
    var loaiCamChon by remember { mutableStateOf(phieuDangSua?.tenLoaiCam ?: "") }
    var maLoaiCamChon by remember { mutableStateOf(phieuDangSua?.maLoaiCam ?: "") }
    var soLuong by remember { mutableStateOf(phieuDangSua?.soLuong?.toString() ?: "") }
    var ngayNhap by remember { mutableStateOf(phieuDangSua?.ngayNhap ?: "") }
    var ghiChu by remember { mutableStateOf(phieuDangSua?.ghiChu ?: "") }
    var dropdownMo by remember { mutableStateOf(false) }

    // DatePicker — khởi tạo millis từ ngày phiếu đang sửa nếu có
    var showDatePicker by remember { mutableStateOf(false) }
    val initialMillis = remember {
        val existingDate = phieuDangSua?.ngayNhap
        if (!existingDate.isNullOrBlank()) {
            runCatching {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(existingDate)?.time
            }.getOrNull() ?: System.currentTimeMillis()
        } else System.currentTimeMillis()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        ngayNhap = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(luuThanhCong) {
        if (luuThanhCong) {
            vm.resetTrangThai()
            onNavigate("danhsach")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (phieuDangSua != null) "Chỉnh sửa phiếu" else "Quản lý nhập cám",
                        color = QlDarkGreen, fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.resetTrangThai()
                        onNavigate("danhsach")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = QlDarkGreen)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            vm.resetTrangThai()
                            onNavigate("danhsach")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        border = BorderStroke(1.dp, QlDarkGreen)
                    ) { Text("Hủy") }

                    Button(
                        onClick = {
                            vm.luuPhieu(
                                maLoaiCam = maLoaiCamChon,
                                tenLoaiCam = loaiCamChon,
                                soLuong = soLuong,
                                ngayNhap = ngayNhap,
                                ghiChu = ghiChu
                            )
                        },
                        enabled = !dangLuu,
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = QlDarkGreen)
                    ) {
                        if (dangLuu) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (phieuDangSua != null) "Cập nhật phiếu" else "Lưu phiếu nhập")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                if (phieuDangSua != null) "Chỉnh sửa phiếu nhập" else "Tạo phiếu nhập mới",
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            Text("Vui lòng nhập đầy đủ thông tin lô cám.", color = QlGrayText)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QlDarkGreen, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (phieuDangSua != null) "CHỈNH SỬA" else "NHẬP MỚI",
                    color = Color.White, fontWeight = FontWeight.Bold
                )
            }

            // Thông báo lỗi
            if (loi != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDED)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = loi ?: "",
                        color = Color(0xFFC25745),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Dropdown loại cám
            Text("Loại cám", fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(
                expanded = dropdownMo,
                onExpandedChange = { dropdownMo = !dropdownMo }
            ) {
                OutlinedTextField(
                    value = if (loaiCamChon.isBlank()) "" else "[$maLoaiCamChon] $loaiCamChon",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    placeholder = { Text("Chọn loại cám", color = QlGrayText) },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = QlGrayBorder)
                )
                ExposedDropdownMenu(
                    expanded = dropdownMo,
                    onDismissRequest = { dropdownMo = false }
                ) {
                    if (danhSachLoai.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Chưa có loại cám. Thêm ở Danh mục.", color = QlGrayText) },
                            onClick = { dropdownMo = false }
                        )
                    } else {
                        danhSachLoai.forEach { loai ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(loai.tenLoaiCam, fontWeight = FontWeight.Medium)
                                        Text(loai.maTag, fontSize = 12.sp, color = QlGrayText)
                                    }
                                },
                                onClick = {
                                    loaiCamChon = loai.tenLoaiCam
                                    maLoaiCamChon = loai.maTag
                                    dropdownMo = false
                                }
                            )
                        }
                    }
                }
            }

            // Số lượng
            Text("Số lượng (bao)", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = soLuong,
                onValueChange = { soLuong = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(0.5f),
                placeholder = { Text("0", color = QlGrayText) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = QlGrayBorder)
            )

            // Ngày nhập — mở DatePickerDialog
            Text("Ngày nhập", fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, QlGrayBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (ngayNhap.isBlank()) QlGrayText else Color.Black
                )
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (ngayNhap.isBlank()) "Chọn ngày nhập" else ngayNhap,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // Ghi chú
            Text("Ghi chú", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = ghiChu,
                onValueChange = { ghiChu = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Nhập ghi chú") },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = QlGrayBorder)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuanLyNhapCamScreen() {
    QuanLyCamTheme {
        QuanLyNhapCamScreen()
    }
}
