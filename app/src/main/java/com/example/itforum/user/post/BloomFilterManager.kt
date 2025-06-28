package com.example.itforum.user.post

import android.content.SharedPreferences
import com.google.common.hash.BloomFilter
import com.google.common.hash.Funnels
import java.io.*
import java.nio.charset.StandardCharsets
class BloomFilterManager(private val prefs: SharedPreferences) {
    private val bloom: BloomFilter<CharSequence> = loadBloomFilter()
    private fun loadBloomFilter(): BloomFilter<CharSequence> {
        val serializedFilter = prefs.getString("bloom_filter", null)
        return if (serializedFilter != null) {
            try{
                val bytes=android.util.Base64.decode(serializedFilter,android.util.Base64.DEFAULT)
                ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as BloomFilter<CharSequence> }
            }catch (e:Exception){
                createNewFilter()
            }
        }else{
            createNewFilter()

        }
    }
    private fun saveBloomFilter(){
        val byteOutput=ByteArrayOutputStream()
        ObjectOutputStream(byteOutput).use { it.writeObject(bloom) }
        val encoded=android.util.Base64.encodeToString(byteOutput.toByteArray(),android.util.Base64.DEFAULT)
        prefs.edit().putString("bloom_filter",encoded).apply()
    }
    private fun createNewFilter():BloomFilter<CharSequence>{
        return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),100000,0.01)
    }
    fun markAsViewed(postId:String){
        bloom.put(postId)
        saveBloomFilter()
    }
    fun isViewed(postId: String):Boolean{
        return bloom.mightContain(postId)
    }
}