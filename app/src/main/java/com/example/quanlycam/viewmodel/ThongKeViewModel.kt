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
    val phanBoLoai: List<Pair<String, Int>> = emptyList(),
    val bienDongTuan: List<Int> = emptyList(),
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

    // Bộ lọc ngày tùy chỉnh (null = không dùng bộ lọc ngày)
    private val _tuNgay = MutableStateFlow<Calendar?>(null)
    val tuNgay: StateFlow<Calendar?> = _tuNgay

    private val _denNgay = MutableStateFlow<Calendar?>(null)
    val denNgay: StateFlow<Calendar?> = _denNgay

    // true = đang dùng bộ lọc ngày tùy chỉnh
    private val _dungLocNgay = MutableStateFlow(false)
    val dungLocNgay: StateFlow<Boolean> = _dungLocNgay

    private val _tatCaPhieu: StateFlow<List<PhieuNhapCam>> = repo.getPhieuList()
        .catch { e ->
            Log.e("ThongKeVM", "Lỗi: ${e.message}", e)
            _loi.value = "Không tải được dữ liệu: ${e.message}"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val thongKe: StateFlow<ThongKeData> = combine(
        _tatCaPhieu, _kyChon, _tuNgay, _denNgay, _dungLocNgay
    ) { list, ky, tuNgay, denNgay, dungLocNgay ->
        if (dungLocNgay && tuNgay != null && denNgay != null) {
            tinhThongKeTheoNgay(list, tuNgay, denNgay)
        } else {
            tinhThongKe(list, ky)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThongKeData())

    fun chonKy(ky: Int) {
        _kyChon.value = ky
        _dungLocNgay.value = false
    }

    fun datLocNgay(tuNgay: Calendar, denNgay: Calendar) {
        val tu = (tuNgay.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val den = (denNgay.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        _tuNgay.value = tu
        _denNgay.value = den
        _dungLocNgay.value = true
    }

    fun xoaLocNgay() {
        _tuNgay.value = null
        _denNgay.value = null
        _dungLocNgay.value = false
    }

    private fun tinhThongKeTheoNgay(list: List<PhieuNhapCam>, tuNgay: Calendar, denNgay: Calendar): ThongKeData {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Chuẩn hóa tuNgay/denNgay về đầu/cuối ngày để tránh lệch giờ
        val tuCal = tuNgay.normalized(startOfDay = true)
        val denCal = denNgay.normalized(startOfDay = false)

        val phieuLoc = list.filter { p ->
            val cal = p.toCalendar(fmt) ?: return@filter false
            !cal.before(tuCal) && !cal.after(denCal)
        }
        val tongSoLuong = phieuLoc.sumOf { it.soLuong }
        val phanBo = phieuLoc
            .groupBy { it.tenLoaiCam }
            .map { (ten, phieus) -> ten to phieus.sumOf { it.soLuong } }
            .sortedByDescending { it.second }

        val soNgay = ((denCal.timeInMillis - tuCal.timeInMillis) / 86_400_000L).toInt() + 1
        val bienDong = if (soNgay <= 31) {
            val buoc = (soNgay / 4).coerceAtLeast(1)
            (0 until 4).map { i ->
                val start = Calendar.getInstance().apply {
                    timeInMillis = tuCal.timeInMillis
                    add(Calendar.DAY_OF_YEAR, i * buoc)
                }.normalized(startOfDay = true)
                val end = if (i < 3) Calendar.getInstance().apply {
                    timeInMillis = tuCal.timeInMillis
                    add(Calendar.DAY_OF_YEAR, (i + 1) * buoc - 1)
                }.normalized(startOfDay = false) else denCal

                phieuLoc.filter { p ->
                    val cal = p.toCalendar(fmt) ?: return@filter false
                    !cal.before(start) && !cal.after(end)
                }.sumOf { it.soLuong }
            }
        } else listOf(0, 0, 0, 0)

        return ThongKeData(
            tongSoLuong = tongSoLuong,
            tongPhieu = phieuLoc.size,
            phanBoLoai = phanBo,
            bienDongTuan = bienDong,
            soLuongKyTruoc = 0
        )
    }

    private fun tinhThongKe(list: List<PhieuNhapCam>, ky: Int): ThongKeData {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = Calendar.getInstance()

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
        val phanBo = phieuKyNay
            .groupBy { it.tenLoaiCam }
            .map { (ten, phieus) -> ten to phieus.sumOf { it.soLuong } }
            .sortedByDescending { it.second }

        val bienDong = (1..4).map { tuan ->
            phieuKyNay.filter { p ->
                val date = runCatching { fmt.parse(p.ngayNhap) }.getOrNull() ?: return@filter false
                val cal = Calendar.getInstance().apply { time = date }
                val ngay = cal.get(Calendar.DAY_OF_MONTH)
                when (tuan) {
                    1 -> ngay in 1..7
                    2 -> ngay in 8..14
                    3 -> ngay in 15..21
                    4 -> ngay >= 22
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

// Chuẩn hóa Calendar về đầu ngày (00:00:00) hoặc cuối ngày (23:59:59)
private fun Calendar.normalized(startOfDay: Boolean): Calendar =
    (this.clone() as Calendar).apply {
        if (startOfDay) {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        } else {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
    }

// Parse ngày phiếu về Calendar chuẩn hóa đầu ngày
private fun PhieuNhapCam.toCalendar(fmt: SimpleDateFormat): Calendar? {
    val date = runCatching { fmt.parse(this.ngayNhap) }.getOrNull() ?: return null
    return Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
}
