package com.example.itforum.user.FilterWords

import android.content.Context

//kiểm tra từ cấm
object WordFilter {
    private val bannedWords = listOf(
        // Nhóm tục tĩu / thô tục
        "đm", "vcl", "vkl", "clgt", "má", "địt", "chó", "cặc", "lồn", "đụ", "đéo", "dm", "fuck", "shit", "bitch",

        // Nhóm xúc phạm / miệt thị
        "ngu", "óc chó", "đần", "đần độn", "thứ rác", "vô học", "dốt", "khùng", "khốn", "thằng hâm", "con điên", "ngu xuẩn",

        // Nhóm phân biệt / xúc phạm nhóm
        "bóng", "pê đê", "mất dạy", "giống chó", "xàm lol", "xàm lông", "bệnh hoạn", "dị hợm", "loạn dục", "mất não",

        // Nhóm tục hóa/gợi dục
        "hiếp", "gái gọi", "gái bao", "xxx", "jav", "sex", "loạn luân", "thô bỉ", "thô tục", "súc vật","đéo",

        // Nhóm chơi chữ lách luật
        "lồn", "cặc", "vl", "ngu vãi", "óc lợn", "đĩ", "phò", "dâm", "râm", "rape", "gạ tình"
    )
    fun containsBannedWordsAndLog( userId: String, input: String): Boolean {
        val hasBadWord = bannedWords.any { word ->
            Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE).containsMatchIn(input)
        }
        if (hasBadWord) {
            ViolationLogger.log( userId, input)
        }
        return hasBadWord
    }

}