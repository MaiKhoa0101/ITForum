package com.example.itforum.user.utilities.chat

import retrofit2.http.Body
import retrofit2.http.POST

data class AiRequest(val prompt: String)
data class AiResponse(val response: String)

interface ChatAiService {
    @POST("chat-ai/chat")// đường dẫn endpoint bên BE
    suspend fun getChatResponse(@Body request: AiRequest): AiResponse
}
