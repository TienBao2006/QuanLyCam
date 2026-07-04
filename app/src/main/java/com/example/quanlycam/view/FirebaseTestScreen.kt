package com.example.quanlycam.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

enum class TestStatus { CHO, THANH_CONG, THAT_BAI }

data class TestResult(
    val ten: String,
    val status: TestStatus = TestStatus.CHO,
    val chiTiet: String = ""
)

@Composable
fun FirebaseTestScreen(onBack: () -> Unit = {}) {
    var results by remember {
        mutableStateOf(
            listOf(
                TestResult("1. Khởi tạo Firestore"),
                TestResult("2. Ghi dữ liệu test"),
                TestResult("3. Đọc dữ liệu test"),
                TestResult("4. Xóa dữ liệu test")
            )
        )
    }
    var dangChay by remember { mutableStateOf(false) }

    fun capNhat(index: Int, status: TestStatus, chiTiet: String = "") {
        results = results.toMutableList().also {
            it[index] = it[index].copy(status = status, chiTiet = chiTiet)
        }
    }

    fun chayTest() {
        dangChay = true
        results = results.map { it.copy(status = TestStatus.CHO, chiTiet = "") }

        try {
            // Test 1: Khởi tạo Firestore
            val db = FirebaseFirestore.getInstance()
            capNhat(0, TestStatus.THANH_CONG, "getInstance() OK")

            // Test 2: Ghi dữ liệu
            val ref = db.collection("_firebaseTest").document("testDoc")
            val testData = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "message" to "test_ok"
            )
            ref.set(testData)
                .addOnSuccessListener {
                    capNhat(1, TestStatus.THANH_CONG, "set() thành công")

                    // Test 3: Đọc dữ liệu
                    ref.get()
                        .addOnSuccessListener { doc ->
                            val msg = doc.getString("message")
                            if (msg == "test_ok") {
                                capNhat(2, TestStatus.THANH_CONG, "Đọc OK: message=$msg")
                            } else {
                                capNhat(2, TestStatus.THAT_BAI, "Dữ liệu không khớp: $msg")
                            }

                            // Test 4: Xóa
                            ref.delete()
                                .addOnSuccessListener {
                                    capNhat(3, TestStatus.THANH_CONG, "delete() OK")
                                    dangChay = false
                                }
                                .addOnFailureListener { e ->
                                    capNhat(3, TestStatus.THAT_BAI, e.message ?: "unknown")
                                    dangChay = false
                                }
                        }
                        .addOnFailureListener { e ->
                            capNhat(2, TestStatus.THAT_BAI, e.message ?: "unknown")
                            dangChay = false
                        }
                }
                .addOnFailureListener { e ->
                    capNhat(1, TestStatus.THAT_BAI, e.message ?: "unknown error")
                    dangChay = false
                }

        } catch (e: Exception) {
            capNhat(0, TestStatus.THAT_BAI, e.message ?: "Exception")
            dangChay = false
        }
    }

    LaunchedEffect(Unit) { chayTest() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("Firestore Connection Test", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF114D35))
        Text("project: quanlycam", fontSize = 12.sp, color = Color.Gray)

        HorizontalDivider()

        results.forEach { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (result.status) {
                        TestStatus.THANH_CONG -> Color(0xFFE8F5E9)
                        TestStatus.THAT_BAI   -> Color(0xFFFFEBEE)
                        TestStatus.CHO        -> Color(0xFFF3F4F6)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    when (result.status) {
                        TestStatus.THANH_CONG -> Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        TestStatus.THAT_BAI   -> Icon(Icons.Default.Error, null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                        TestStatus.CHO        -> Icon(Icons.Default.HourglassEmpty, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(result.ten, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        if (result.chiTiet.isNotBlank()) {
                            Text(
                                result.chiTiet, fontSize = 12.sp,
                                color = when (result.status) {
                                    TestStatus.THANH_CONG -> Color(0xFF1B5E20)
                                    TestStatus.THAT_BAI   -> Color(0xFFB71C1C)
                                    TestStatus.CHO        -> Color.Gray
                                }
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        val coLoi = results.any { it.status == TestStatus.THAT_BAI }
        if (coLoi) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Hướng dẫn sửa lỗi:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Vào Firebase Console → Firestore Database → Rules", fontSize = 12.sp)
                    Text(
                        "• Đổi rules thành:\n" +
                        "  rules_version = '2';\n" +
                        "  service cloud.firestore {\n" +
                        "    match /databases/{db}/documents {\n" +
                        "      match /{doc=**} {\n" +
                        "        allow read, write: if true;\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }",
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Text("• Kiểm tra kết nối internet của thiết bị/máy ảo", fontSize = 12.sp)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { chayTest() },
                enabled = !dangChay,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF114D35))
            ) {
                if (dangChay) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Chạy lại test")
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Quay lại")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
