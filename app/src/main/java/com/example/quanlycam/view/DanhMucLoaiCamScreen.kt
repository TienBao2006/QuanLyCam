package com.example.quanlycam.view

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlycam.ui.theme.QuanLyCamTheme
import com.example.quanlycam.ui.theme.DarkGreen
import com.example.quanlycam.ui.theme.BgColor
import com.example.quanlycam.ui.theme.GrayBorder
import com.example.quanlycam.ui.theme.GrayText
import com.example.quanlycam.ui.theme.ActiveBlue
import com.example.quanlycam.viewmodel.DanhMucLoaiCamViewModel

private val TagGreenBg = Color(0xFFA7F3D0)
private val TagGreenText = Color(0xFF065F46)
private val TextDark = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanhMucLoaiCamScreen(
    onNavigate: (String) -> Unit = {},
    vm: DanhMucLoaiCamViewModel = viewModel()
) {
    val danhSach by vm.danhSach.collectAsStateWithLifecycle()
    val loiFirebase by vm.loi.collectAsStateWithLifecycle()

    // Dialog thêm loại cám mới
    var showDialog by remember { mutableStateOf(false) }
    var inputMaTag by remember { mutableStateOf("") }
    var inputTenLoai by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thêm loại cám mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputMaTag,
                        onValueChange = { inputMaTag = it },
                        label = { Text("Mã tag (VD: F902)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputTenLoai,
                        onValueChange = { inputTenLoai = it },
                        label = { Text("Tên loại cám") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputMaTag.isNotBlank() && inputTenLoai.isNotBlank()) {
                            vm.them(inputMaTag.trim(), inputTenLoai.trim())
                            inputMaTag = ""
                            inputTenLoai = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) { Text("Lưu") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(36.dp))
                Text(
                    text = "Danh mục loại cám",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = ActiveBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", modifier = Modifier.size(24.dp))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F4F6),
                modifier = Modifier.border(1.dp, GrayBorder)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ", fontSize = 11.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = ActiveBlue,
                        selectedIconColor = Color.White
                    ),
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Danh sách", fontSize = 11.sp) },
                    selected = false,
                    onClick = { onNavigate("danhsach") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Thống kê", fontSize = 11.sp) },
                    selected = false,
                    onClick = { onNavigate("thongke") }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tổng số: ${danhSach.size} loại",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Dữ liệu realtime từ Firebase",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Thêm loại cám\nmới",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            if (loiFirebase != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDED)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC25745), modifier = Modifier.size(18.dp))
                            Text(loiFirebase ?: "", color = Color(0xFFC25745), fontSize = 13.sp)
                        }
                    }
                }
            }

            if (danhSach.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có loại cám nào.\nNhấn + để thêm mới.", color = GrayText, fontSize = 14.sp)
                    }
                }
            }

            items(danhSach, key = { it.id }) { cam ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GrayBorder, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TagGreenBg)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = cam.maTag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TagGreenText
                                )
                            }
                            Text(
                                text = cam.tenLoaiCam,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                lineHeight = 22.sp
                            )
                        }
                        IconButton(onClick = { vm.xoa(cam.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFC25745))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GrayBorder.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ActiveBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Các thay đổi về đơn giá sẽ ảnh hưởng trực tiếp đến các đơn hàng nhập cám mới từ thời điểm lưu chỉnh sửa.",
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DanhMucLoaiCamScreenPreview() {
    QuanLyCamTheme {
        DanhMucLoaiCamScreen()
    }
}
