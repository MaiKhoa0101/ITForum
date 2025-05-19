package com.example.itforum.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun IntroScreen(navController: NavController) {
    val pageCount = 5
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            IntroPageContent(page)
        }

        // Thay Row bằng Column để xử lý riêng từng trường hợp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                pagerState.currentPage == 0 -> {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),


                        shape = RoundedCornerShape(20.dp),
                        colors=ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF6B9BF6),
                        contentColor= Color.Black)
                    ) {
                        Text("Bắt đầu nào",
                            fontWeight = FontWeight.Bold,
                            fontSize=16.sp
                            )
                    }
                }

                pagerState.currentPage == pageCount - 1 -> {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp), // Thêm padding ngang
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        Button(onClick = { navController.navigate("login") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(48.dp),


                            shape = RoundedCornerShape(20.dp),
                            colors=ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF6B9BF6),
                                contentColor= Color.Black)) {
                            Text("Đăng nhập", fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { navController.navigate("com/example/itforum/register") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(48.dp)
                                .border(
                                    width = 5.dp,
                                    color = Color(0xBABABA),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors=ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor= Color.Black),

                            border = ButtonDefaults.outlinedBorder.copy(
                                width = 0.dp // Tắt viền mặc định
                            )

                        ) {
                            Text("Đăng ký",fontWeight=FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFD2D2D2),
                                contentColor = Color.Black
                            )

                            ) {
                            Text("Quay lại")
                        }

                        Button(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF6B9BF6),
                                contentColor = Color.Black
                            )

                            ) {
                            Text("Tiếp")
                        }
                    }
                }
            }
        }
    }
}