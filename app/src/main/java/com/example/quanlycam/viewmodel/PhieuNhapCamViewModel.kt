package com.example.quanlycam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.data.repository.PhieuNhapCamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhieuNhapCamViewModel : ViewModel() {

    private val repository = PhieuNhapCamRepository()
    private val _loi = MutableStateFlow<String?>(null)
    val loi: StateFlow<String?> = _loi
    private val _list = MutableStateFlow<List<PhieuNhapCam>>(emptyList())
    val list: StateFlow<List<PhieuNhapCam>> = _list.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPhieuList().collect {
                _list.value = it
            }
        }
    }
    val danhSach: StateFlow<List<PhieuNhapCam>> = repository.getPhieuList()
        .catch { e ->
            Log.e("DanhSachVM", "Lỗi Firebase: ${e.message}", e)
            _loi.value = "Không thể tải dữ liệu: ${e.message}"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun xoa(id: String) {
        viewModelScope.launch {
            repository.xoa(id).onFailure { e ->
                Log.e("DanhSachVM", "Lỗi xóa: ${e.message}", e)
                _loi.value = "Xóa thất bại: ${e.message}"
            }
        }
    }
}