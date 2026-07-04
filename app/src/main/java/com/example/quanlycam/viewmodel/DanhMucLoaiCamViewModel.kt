package com.example.quanlycam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.LoaiCam
import com.example.quanlycam.data.repository.LoaiCamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DanhMucLoaiCamViewModel(
    private val repo: LoaiCamRepository = LoaiCamRepository()
) : ViewModel() {

    private val _loi = MutableStateFlow<String?>(null)
    val loi: StateFlow<String?> = _loi

    val danhSach: StateFlow<List<LoaiCam>> = repo.getLoaiCamList()
        .catch { e ->
            Log.e("DanhMucVM", "Lỗi Firebase: ${e.message}", e)
            _loi.value = "Không thể tải dữ liệu: ${e.message}"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun them(maTag: String, tenLoaiCam: String) {
        viewModelScope.launch {
            repo.them(LoaiCam(maTag = maTag, tenLoaiCam = tenLoaiCam))
                .onFailure { e ->
                    Log.e("DanhMucVM", "Lỗi thêm: ${e.message}", e)
                    _loi.value = "Thêm thất bại: ${e.message}"
                }
        }
    }

    fun xoa(id: String) {
        viewModelScope.launch {
            repo.xoa(id).onFailure { e ->
                Log.e("DanhMucVM", "Lỗi xóa: ${e.message}", e)
                _loi.value = "Xóa thất bại: ${e.message}"
            }
        }
    }
}
