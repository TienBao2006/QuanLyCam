package com.example.quanlycam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.data.repository.PhieuNhapCamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuanLyNhapCamViewModel(
    private val repo: PhieuNhapCamRepository = PhieuNhapCamRepository()
) : ViewModel() {

    private val _luuThanhCong = MutableStateFlow(false)
    val luuThanhCong: StateFlow<Boolean> = _luuThanhCong

    private val _dangLuu = MutableStateFlow(false)
    val dangLuu: StateFlow<Boolean> = _dangLuu

    private val _loi = MutableStateFlow<String?>(null)
    val loi: StateFlow<String?> = _loi

    // Phiếu đang được chỉnh sửa (null = tạo mới)
    private val _phieuDangSua = MutableStateFlow<PhieuNhapCam?>(null)
    val phieuDangSua: StateFlow<PhieuNhapCam?> = _phieuDangSua

    fun batDauSua(phieu: PhieuNhapCam) {
        _phieuDangSua.value = phieu
    }

    fun luuPhieu(
        maLoaiCam: String,
        tenLoaiCam: String,
        soLuong: String,
        ngayNhap: String,
        ghiChu: String
    ) {
        val soLuongInt = soLuong.trim().toIntOrNull()
        if (maLoaiCam.isBlank() || soLuongInt == null || soLuongInt <= 0 || ngayNhap.isBlank()) {
            _loi.value = "Vui lòng nhập đầy đủ loại cám, số lượng và ngày nhập."
            return
        }
        viewModelScope.launch {
            _dangLuu.value = true
            _loi.value = null

            val dangSua = _phieuDangSua.value
            if (dangSua != null) {
                // Cập nhật phiếu đã có
                val phieuCapNhat = dangSua.copy(
                    maLoaiCam = maLoaiCam,
                    tenLoaiCam = tenLoaiCam,
                    soLuong = soLuongInt,
                    ngayNhap = ngayNhap,
                    ghiChu = ghiChu
                )
                val result = repo.sua(phieuCapNhat)
                _dangLuu.value = false
                result.onSuccess { _luuThanhCong.value = true }
                result.onFailure { _loi.value = "Lỗi cập nhật: ${it.message}" }
            } else {
                // Tạo phiếu mới với mã tự động theo ngày giờ phút giây
                val maPhieu = "PN-" + SimpleDateFormat("ddMMyyyy-HHmmss", Locale.getDefault()).format(Date())
                val phieu = PhieuNhapCam(
                    maPhieu ,
                    maLoaiCam = maLoaiCam,
                    tenLoaiCam = tenLoaiCam,
                    soLuong = soLuongInt,
                    ngayNhap = ngayNhap,
                    ghiChu = ghiChu,
                    taoLuc = System.currentTimeMillis()
                )
                val result = repo.them(phieu)
                _dangLuu.value = false
                result.onSuccess { _luuThanhCong.value = true }
                result.onFailure { _loi.value = "Lỗi lưu dữ liệu: ${it.message}" }
            }
        }
    }

    fun resetTrangThai() {
        _luuThanhCong.value = false
        _loi.value = null
        _phieuDangSua.value = null
    }
}
