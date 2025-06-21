package com.example.itforum.user.FilterWords

import android.content.Context
import android.widget.Toast
import com.example.itforum.user.FilterWords.ToastHelper.appContext

object ToastHelper {
    private var appContext: Context?=null
    fun init(context: Context){
        appContext=context.applicationContext
    }
    fun show(message:String){
        appContext?.let{
            Toast.makeText(it,message,Toast.LENGTH_SHORT).show()
        }
    }
}