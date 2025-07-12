package com.example.itforum.service

import android.util.Log
import com.example.itforum.retrofit.RetrofitInstance
import retrofit2.Response

object AuthRepository {
    suspend fun sendOtp(email: String): Result<String> {
        return try {
            val response = RetrofitInstance.authService.sendOtp(mapOf("email" to email))
            if (response.isSuccessful) {
                Log.d("OTP", "Gửi OTP thành công: ${response.body()}")
                Result.success(response.body() ?: "OTP sent")
            } else {
                Log.e("OTP", "Gửi OTP thất bại: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e("OTP", "Lỗi khi gửi OTP", e)
            Result.failure(e)
        }
    }


    suspend fun verifyOtp(email: String, otp: String): Result<String> {
        return try {
            val response = RetrofitInstance.authService.verifyOtp(mapOf("email" to email, "otp" to otp))
            if (response.isSuccessful) {
                Result.success(response.body() ?: "OTP hợp lệ")
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Lỗi không xác định"
                } catch (e: Exception) {
                    "Lỗi xử lý phản hồi"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<String> {
        return try {
            val body = mapOf("email" to email, "otp" to otp, "newPassword" to newPassword)
            val response = RetrofitInstance.authService.resetPassword(body)
            if (response.isSuccessful) Result.success(response.body() ?: "Đặt lại mật khẩu thành công")
            else Result.failure(Exception(response.errorBody()?.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
