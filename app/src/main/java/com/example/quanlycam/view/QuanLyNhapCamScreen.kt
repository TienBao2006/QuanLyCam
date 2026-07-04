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
    // Danh sách loại cám từ Firebase để hiển thị dropdown
    val danhSachLoai by vmLoai.danhSach.collectAsStateWithLifecycle()

    var loaiCamChon by remember { mutableStateOf("") }   // tenLoaiCam được chọn
    var maLoaiCamChon by remember { mutableStateOf("") } // maTag được chọn
    var dropdownMo by remember { mutableStateOf(false) }

    var soLuong by remember { mutableStateOf("") }
    var ngayNhap by remember { mutableStateOf("") }
    var ghiChu by remember { mutableStateOf("") }

    val dangLuu by vm.dangLuu.collectAsStateWithLifecycle()
    val luuThanhCong by vm.luuThanhCong.collectAsStateWithLifecycle()
    val loi by vm.loi.collectAsStateWithLifecycle()
    val phieuDangSua by vm.phieuDangSua.collectAsStateWithLifecycle()

    // Load dữ liệu vào form khi đang sửa
    LaunchedEffect(phieuDangSua) {
        phieuDangSua?.let { p ->
            loaiCamChon = p.tenLoaiCam
            maLoaiCamChon = p.maLoaiCam
            soLuong = p.soLuong.toString()
            ngayNhap = p.ngayNhap
            ghiChu = p.ghiChu
        }
    }

    LaunchedEffect(luuThanhCong) {
        if (luuThanhCong) {
            vm.resetTrangThai()
            // Reset form
            loaiCamChon = ""; maLoaiCamChon = ""
            soLuong = ""; ngayNhap = ""; ghiChu = ""
            onNavigate("danhsach")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Quản lý nhập cám", color = QlDarkGreen, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("danhsach") }) {
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
                            loaiCamChon = ""; maLoaiCamChon = ""
                            soLuong = ""; ngayNhap = ""; ghiChu = ""
                            onNavigate("danhsach")
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
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
                        modifier = Modifier.weight(2f).height(48.dp),
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

            // Dropdown chọn loại cám từ Firebase
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
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    },
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

            // Ngày nhập
            Text("Ngày nhập", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = ngayNhap,
                onValueChange = { ngayNhap = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("dd/MM/yyyy") },
                trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = QlGrayBorder)
            )

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
