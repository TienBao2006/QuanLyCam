package com.example.quanlycam.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quanlycam.ui.theme.GrayBorder

// Tab hiện tại đang active
enum class BottomTab { TRANG_CHU, LOAI_CAM, DANH_SACH, THONG_KE }

private val ActiveColor   = Color(0xFFC25745)
private val InactiveColor = Color(0xFF6B7280)
private val NavBg         = Color(0xFFEFE7DC)

private data class TabItem(
    val tab: BottomTab,
    val icon: ImageVector,
    val label: String
)

private val tabs = listOf(
    TabItem(BottomTab.TRANG_CHU,  Icons.Default.Home,     "Trang chủ"),
    TabItem(BottomTab.LOAI_CAM,   Icons.Default.Grass,    "Loại cám"),
    TabItem(BottomTab.DANH_SACH,  Icons.Default.List,     "Danh sách"),
    TabItem(BottomTab.THONG_KE,   Icons.Default.BarChart, "Thống kê"),
)

@Composable
fun AppBottomBar(
    current: BottomTab,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = NavBg,
        modifier = Modifier.border(1.dp, GrayBorder)
    ) {
        tabs.forEach { item ->
            val selected = item.tab == current
            val tint = if (selected) ActiveColor else InactiveColor

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = tint,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFEADCC9),
                    selectedIconColor = ActiveColor,
                    unselectedIconColor = InactiveColor
                ),
                onClick = {
                    when (item.tab) {
                        BottomTab.TRANG_CHU -> { /* rỗng */ }
                        BottomTab.LOAI_CAM  -> onNavigate("danhmuc")
                        BottomTab.DANH_SACH -> onNavigate("danhsach")
                        BottomTab.THONG_KE  -> onNavigate("thongke")
                    }
                }
            )
        }
    }
}
