package com.example.itforum.utilities.chat

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
                "model": "mistralai/mistral-7b-instruct",
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
            .addHeader("HTTP-Referer", "https://openrouter.ai") // thay bằng domain của bạn hoặc để mặc định
            .addHeader("X-Title", "ChatApp")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) {
                    callback(null)
                    return
                }

                try {
                    val jsonObj = JSONObject(responseBody)
                    val message = jsonObj
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    callback(message)
                } catch (e: Exception) {
                    callback(null)
                }
            }
        })
    }
}