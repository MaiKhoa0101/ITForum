package com.example.itforum.intro

import androidx.compose.runtime.Composable
import com.example.itforum.intro.pages.IntroPage0
import com.example.itforum.intro.pages.IntroPage1
import com.example.itforum.intro.pages.IntroPage2
import com.example.itforum.intro.pages.IntroPage3
import com.example.itforum.intro.pages.IntroPage4
@Composable
fun IntroPageContent(page:Int){
    when(page){
        0->IntroPage0()
        1->IntroPage1()
        2->IntroPage2()
        3->IntroPage3()
        4->IntroPage4()
    }
}
