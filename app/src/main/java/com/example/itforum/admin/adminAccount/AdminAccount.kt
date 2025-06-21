package com.example.itforum.admin.adminAccount


import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.modelData.response.UserResponse
import com.example.itforum.user.post.icontext
import com.example.itforum.user.profile.viewmodel.UserViewModel

@Composable
fun AccountManagementScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        userViewModel.getAllUser()
    }
    val listUser by userViewModel.listUser.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ userId ->
            navHostController.navigate("user_detail/$userId")
        }),
        icontext(Icons.Default.Delete,"Xóa")
    )
    val filterOptions = mapOf(
        "Trạng thái" to listOf("Hoạt động", "Bị khóa")
    )
    AdminScreenLayout(
        title = "Quản lý khiếu nại người dùng",
        itemCount = listUser.size,
        modifier = modifier,
        searchTable = { searchText ->
            val dataFiltered = listUser.filter { item ->
                searchText.isBlank() || listOf(
                    item.id,
                    item.phone,
                    item.email,
                    item.name
                ).any { it.contains(searchText, ignoreCase = true) }
            }
            return@AdminScreenLayout dataFiltered
        },
        filterOptions = filterOptions,
        filterField = { field, value, data ->
            data as List<UserResponse>
            val dataFiltered = when (field) {
                "Trạng thái" -> {
                    when (value) {
                        "Đang hoạt động" -> data.filter { it.isBanned == false }
                        "Bị khóa" -> data.filter { it.isBanned == true }
                        else -> data
                    }
                }
                else -> data
            }
            return@AdminScreenLayout dataFiltered
        },
        table = { dataFiltered ->
            TableData(
                headers = listOf(
                    "ID",
                    "Người dùng",
                    "email",
                    "SĐT",
                    "Trạng thái",
                    "Tùy chỉnh"
                ),
                rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                menuOptions = menuOptions,
                sharedPreferences = sharedPreferences
            )
        }
    )

}


