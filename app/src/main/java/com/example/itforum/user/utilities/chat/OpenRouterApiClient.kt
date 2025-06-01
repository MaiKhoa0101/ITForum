package com.example.itforum.utilities.chat

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object OpenRouterApiClient {
    private const val API_KEY = "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" // Thay bằng API Key của bạn
    private val client = OkHttpClient()

    fun generateText(prompt: String, callback: (String?) -> Unit) {
        val json = """
            {
                "model": "openai/gpt-3.5-turbo",

                "messages": [
                    {"role": "user", "content": "$prompt"}
                ]
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .addHeader("Referer", "https://openrouter.ai") // hoặc tên miền thật của bạn
            .addHeader("X-Title", "ChatApp")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenRouter", "❌ API call failed: ${e.message}")
                e.printStackTrace()
                callback("❌ API lỗi: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = try {
                    response.body?.string()
                } catch (e: Exception) {
                    Log.e("OpenRouter", "❌ Lỗi đọc body: ${e.message}")
                    callback("❌ Lỗi đọc phản hồi")
                    return
                }

                Log.d("OpenRouter", "📥 Raw response: $bodyString")

                if (bodyString == null || !response.isSuccessful) {
                    Log.e("OpenRouter", "⚠️ API error: ${response.code} - ${response.message}")
                    callback("❌ Lỗi API: ${response.code} - ${response.message}")
                    return
                }

                try {
                    val json = JSONObject(bodyString)
                    val reply = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    Log.d("OpenRouter", "✅ AI trả lời: $reply")
                    callback(reply)

                } catch (e: Exception) {
                    Log.e("OpenRouter", "❌ JSON parse error: ${e.message}")
                    Log.e("OpenRouter", "❌ Body bị lỗi: $bodyString")
                    e.printStackTrace()
                    callback("❌ JSON lỗi")
                }
            }





//            override fun onFailure(call: Call, e: IOException) {
//                callback(null)
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                if (!response.isSuccessful || responseBody == null) {
//                    callback(null)
//                    return
//                }
//
//                try {
//                    val jsonObj = JSONObject(responseBody)
//                    val message = jsonObj
//                        .getJSONArray("choices")
//                        .getJSONObject(0)
//                        .getJSONObject("message")
//                        .getString("content")
//                    callback(message)
//                } catch (e: Exception) {
//                    callback(null)
//                }
//            }
        })
    }
}