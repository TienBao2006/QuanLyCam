package com.example.quanlycam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.data.repository.PhieuNhapCamRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ThongKeData(
    val tongSoLuong: Int = 0,
    val tongPhieu: Int = 0,
    val phanBoLoai: List<Pair<String, Int>> = emptyList(),   // tenLoaiCam -> soLuong
    val bienDongTuan: List<Int> = emptyList(),               // 4 tuần, mỗi tuần tổng bao
    val soLuongKyTruoc: Int = 0
)

class ThongKeViewModel(
    private val repo: PhieuNhapCamRepository = PhieuNhapCamRepository()
) : ViewModel() {

    private val _loi = MutableStateFlow<String?>(null)
    val loi: StateFlow<String?> = _loi

    // 0=Tháng, 1=Quý, 2=Năm
    private val _kyChon = MutableStateFlow(0)
    val kyChon: StateFlow<Int> = _kyChon

    private val _tatCaPhieu: StateFlow<List<PhieuNhapCam>> = repo.getPhieuList()
        .catch { e ->
            Log.e("ThongKeVM", "Lỗi: ${e.message}", e)
            _loi.value = "Không tải được dữ liệu: ${e.message}"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val thongKe: StateFlow<ThongKeData> = combine(_tatCaPhieu, _kyChon) { list, ky ->
        tinhThongKe(list, ky)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThongKeData())

    fun chonKy(ky: Int) { _kyChon.value = ky }

    private fun tinhThongKe(list: List<PhieuNhapCam>, ky: Int): ThongKeData {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = Calendar.getInstance()

        // Lọc phiếu theo kỳ được chọn
        val phieuKyNay = list.filter { p ->
            val date = runCatching { fmt.parse(p.ngayNhap) }.getOrNull() ?: return@filter false
            val cal = Calendar.getInstance().apply { time = date }
            when (ky) {
                0 -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                     cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                1 -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                     ((cal.get(Calendar.MONTH) / 3) == (now.get(Calendar.MONTH) / 3))
                2 -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                else -> true
            }
        }

        // Lọc phiếu kỳ trước để so sánh
        val phieuKyTruoc = list.filter { p ->
            val date = runCatching { fmt.parse(p.ngayNhap) }.getOrNull() ?: return@filter false
            val cal = Calendar.getInstance().apply { time = date }
            when (ky) {
                0 -> {
                    val prevMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                    cal.get(Calendar.YEAR) == prevMonth.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == prevMonth.get(Calendar.MONTH)
                }
                1 -> {
                    val prevQ = (now.get(Calendar.MONTH) / 3) - 1
                    val yr = if (prevQ < 0) now.get(Calendar.YEAR) - 1 else now.get(Calendar.YEAR)
                    val q = if (prevQ < 0) 3 else prevQ
                    cal.get(Calendar.YEAR) == yr && (cal.get(Calendar.MONTH) / 3) == q
                }
                2 -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) - 1
                else -> false
            }
        }

        val tongSoLuong = phieuKyNay.sumOf { it.soLuong }

        // Phân bổ theo loại cám
        val phanBo = phieuKyNay
            .groupBy { it.tenLoaiCam }
            .map { (ten, phieus) -> ten to phieus.sumOf { it.soLuong } }
            .sortedByDescending { it.second }

        // Biến động 4 tuần trong kỳ (chỉ hợp lý với kỳ = tháng)
        val bienDong = (1..4).map { tuan ->
            phieuKyNay.filter { p ->
                val date = runCatching { fmt.parse(p.ngayNhap) }.getOrNull() ?: return@filter false
                val cal = Calendar.getInstance().apply { time = date }
                val ngayTrongThang = cal.get(Calendar.DAY_OF_MONTH)
                when (tuan) {
                    1 -> ngayTrongThang in 1..7
                    2 -> ngayTrongThang in 8..14
                    3 -> ngayTrongThang in 15..21
                    4 -> ngayTrongThang >= 22
                    else -> false
                }
            }.sumOf { it.soLuong }
        }

        return ThongKeData(
            tongSoLuong = tongSoLuong,
            tongPhieu = phieuKyNay.size,
            phanBoLoai = phanBo,
            bienDongTuan = bienDong,
            soLuongKyTruoc = phieuKyTruoc.sumOf { it.soLuong }
        )
    }
}
