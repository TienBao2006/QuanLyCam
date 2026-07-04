package com.example.quanlycam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.data.repository.PhieuNhapCamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DanhSachNhapCamViewModel(
    private val repo: PhieuNhapCamRepository = PhieuNhapCamRepository()
) : ViewModel() {

    private val _loi = MutableStateFlow<String?>(null)
    val loi: StateFlow<String?> = _loi

    val danhSach: StateFlow<List<PhieuNhapCam>> = repo.getPhieuList()
        .catch { e ->
            Log.e("DanhSachVM", "Lỗi Firebase: ${e.message}", e)
            _loi.value = "Không thể tải dữ liệu: ${e.message}"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun xoa(id: String) {
        viewModelScope.launch {
            repo.xoa(id).onFailure { e ->
                Log.e("DanhSachVM", "Lỗi xóa: ${e.message}", e)
                _loi.value = "Xóa thất bại: ${e.message}"
            }
        }
    }
}
