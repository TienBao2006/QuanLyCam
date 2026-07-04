package com.example.quanlycam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlycam.data.model.PhieuNhapCam
import com.example.quanlycam.data.repository.PhieuNhapCamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhieuNhapCamViewModel : ViewModel() {

    private val repository = PhieuNhapCamRepository()

    private val _list = MutableStateFlow<List<PhieuNhapCam>>(emptyList())
    val list: StateFlow<List<PhieuNhapCam>> = _list.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPhieuList().collect {
                _list.value = it
            }
        }
    }
}