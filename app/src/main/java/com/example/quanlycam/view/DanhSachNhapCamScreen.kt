package com.example.quanlycam.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.quanlycam.viewmodel.PhieuNhapCamViewModel

// --- BẢNG MÀU CHUẨN THEO DESIGN IMAGE_754464.PNG ---
val CreamBg = Color(0xFFF4EDE4)          // Màu nền tổng thể kem beige ấm
val ActiveTabBg = Color(0xFFEADCC9)      // Màu nền Tab được chọn ở BottomBar
val OrangeText = Color(0xFFC25745)       // Màu cam đất đặc trưng cho số liệu/FAB
val MintBg = Color(0xFFD4EAE2)           // Màu nền xanh mint của thẻ Tổng Nhập
val CardBg = Color(0xFFFDFBF7)           // Màu trắng kem của các item card
val GrayBorderColor = Color(0xFFE5E7EB)
val GrayIconColor = Color(0xFF8E8E93)

data class PhieuCamItem(
    val maDon: String,
    val tenCam: String,
    val thoiGian: String,
    val soLuong: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanhSachNhapCamScreen(
    onNavigate: (String) -> Unit = {},
    vm: PhieuNhapCamViewModel = viewModel()
){
    val danhSachDonHang by vm.list.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate("nhap") },
                containerColor = OrangeText,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm mới", modifier = Modifier.size(28.dp))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFEFE7DC),
                modifier = Modifier.border(1.dp, Color(0xFFE5DDD2))
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.DarkGray) },
                    label = { Text("Trang chủ", fontSize = 11.sp, color = Color.DarkGray) },
                    selected = false,
                    onClick = { onNavigate("danhmuc") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null, tint = OrangeText) },
                    label = { Text("Danh sách", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangeText) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(indicatorColor = ActiveTabBg),
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.DarkGray) },
                    label = { Text("Thống kê", fontSize = 11.sp, color = Color.DarkGray) },
                    selected = false,
                    onClick = { onNavigate("thongke") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray) },
                    label = { Text("Cá nhân", fontSize = 11.sp, color = Color.DarkGray) },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBg)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Tìm kiếm mã đơn, loại cám...", color = Color.Gray, fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = GrayBorderColor,
                        unfocusedBorderColor = GrayBorderColor
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("Khoảng thời gian", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            placeholder = { Text("dd/mm/yyyy", fontSize = 13.sp) },
                            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, unfocusedBorderColor = GrayBorderColor),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("", fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            placeholder = { Text("dd/mm/yyyy", fontSize = 13.sp) },
                            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, unfocusedBorderColor = GrayBorderColor),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Loại cám", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            placeholder = { Text("Tất cả", fontSize = 13.sp) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, unfocusedBorderColor = GrayBorderColor),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MintBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TỔNG NHẬP THÁNG 10/2023", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E5B4A))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("12,450 bao", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangeText)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách gần đây", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF262626))
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
                        Text("Xuất báo cáo", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }
                }
            }

            items(danhSachDonHang) { phieu ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color(0xFFE5DDD2),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = phieu.id,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )

                            Row {

                                IconButton(
                                    onClick = {
                                        // TODO: sửa
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        vm.xoa(phieu.id)
                                        // TODO: xóa
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = OrangeText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .border(
                                        1.dp,
                                        GrayIconColor,
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = GrayIconColor,
                                    modifier = Modifier.size(14.dp)
                                )

                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = phieu.tenLoaiCam,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = GrayIconColor
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = phieu.ngayNhap,
                                    fontSize = 13.sp
                                )

                            }

                            Text(
                                text = "${phieu.soLuong}\nbao",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeText,
                                textAlign = TextAlign.End,
                                lineHeight = 20.sp
                            )
                        }

                        if (phieu.ghiChu.isNotBlank()) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ghi chú: ${phieu.ghiChu}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                        }

                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DanhSachNhapCamScreenPreview() {
    MaterialTheme {
        DanhSachNhapCamScreen()
    }
}
