package com.example.quanlycam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.quanlycam.ui.theme.QuanLyCamTheme
import com.example.quanlycam.view.DanhMucLoaiCamScreen
import com.example.quanlycam.view.DanhSachNhapCamScreen
import com.example.quanlycam.view.FirebaseTestScreen
import com.example.quanlycam.view.QuanLyNhapCamScreen
import com.example.quanlycam.view.ThongKeCamScreen
import com.example.quanlycam.viewmodel.QuanLyNhapCamViewModel

class MainActivity : ComponentActivity() {
    // Dùng activity-scoped ViewModel để DanhSachNhapCamScreen và QuanLyNhapCamScreen
    // chia sẻ cùng một instance — cần thiết cho chức năng sửa phiếu
    private val quanLyNhapVm: QuanLyNhapCamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuanLyCamTheme {
                AppNavigation(quanLyNhapVm)
            }
        }
    }
}

@Composable
fun AppNavigation(quanLyNhapVm: QuanLyNhapCamViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "firebasetest") {
        composable("firebasetest") {
            FirebaseTestScreen(onBack = { navController.navigate("danhsach") })
        }
        composable("danhmuc") {
            DanhMucLoaiCamScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("danhsach") {
            DanhSachNhapCamScreen(
                onNavigate = { route ->
                    if (route == "nhap") {
                        navController.navigate("nhap")
                    } else {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

            )
        }
        composable("nhap") {
            QuanLyNhapCamScreen(
                onNavigate = { route ->
                    if (navController.previousBackStackEntry?.destination?.route == route) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(route) {
                            popUpTo("nhap") { inclusive = true }
                        }
                    }
                },
                vm = quanLyNhapVm
            )
        }
        composable("thongke") {
            ThongKeCamScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}
