package com.example.quanlycam.data.model

data class PhieuNhapCam(
    val id: String = "",
    val maPhieu: String = "",
    val maLoaiCam: String = "",
    val tenLoaiCam: String = "",
    val soLuong: Int = 0,
    val ngayNhap: String = "",
    val ghiChu: String = "",
    val taoLuc: Long = 0L
)
