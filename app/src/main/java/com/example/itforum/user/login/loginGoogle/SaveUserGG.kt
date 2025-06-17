package com.example.itforum.user.login.loginGoogle
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun saveUserToFirestore(user: FirebaseUser) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(user.uid)

    userRef.get().addOnSuccessListener { document ->
        if (!document.exists()) {
            val newUser = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to (user.displayName ?: "Người dùng mới"),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "role" to "user",
                "joinedAt" to FieldValue.serverTimestamp()
            )

            userRef.set(newUser)
                .addOnSuccessListener {
                    Log.d("Firestore", "✅ User mới đã được lưu vào Firestore.")
                }
                .addOnFailureListener {
                    Log.e("Firestore", "❌ Lưu user thất bại: ${it.message}")
                }
        } else {
            Log.d("Firestore", "🔁 User đã tồn tại, không cần lưu lại.")
        }
    }.addOnFailureListener {
        Log.e("Firestore", "❌ Không thể kiểm tra user tồn tại: ${it.message}")
    }
}
