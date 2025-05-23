package com.example.itforum.user.home.follow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import com.example.itforum.user.post.PostCard
import com.example.itforum.user.post.postList

@Composable
fun FollowScreen(){
    val mockFollowerList = listOf(
    followModel(name = "Bob Smith", following = "180 followers"),
    followModel("Charlie Nguyen", "150 followers"),
    followModel("Daisy Tran", "220 followers"),
    followModel("Ethan Brown", "95 followers")
    )
    val mockSuggestFollowerList = listOf(
        followModel(name = "Bob Smith", following = "180 followers"),
        followModel("Charlie Nguyen", "150 followers"),
        followModel("Daisy Tran", "220 followers"),
        followModel("Ethan Brown", "95 followers")
    )

    LazyColumn {
        item {
            Text("Following", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        }

        items(mockFollowerList.size) { index ->
            FollowerWidget(followModel = mockFollowerList[index], isFollowing = true)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Suggestions", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        }

        item {
            Column {
                mockSuggestFollowerList.forEachIndexed { index, follower ->
                    FollowerWidget(followModel = follower, isFollowing = false)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Posts from your following ", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(postList.size) { index ->
            PostCard(post = postList[index])
            // separate post box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Black)
            )
        }
    }
}
@Composable
fun FollowerWidget(followModel: followModel, isFollowing: Boolean){
    val btnColor = if(!isFollowing) Color.Green else Color.Red;
    val btnText = if(!isFollowing) "Follow" else "Unfollow";
    val textColor = if(!isFollowing) Color.White else Color.Black;
   Row(

       horizontalArrangement = Arrangement.SpaceBetween,
       verticalAlignment = Alignment.CenterVertically,
       modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 20.dp)
   ) {
       Row(
           verticalAlignment = Alignment.CenterVertically
       ) {
           Image(
               painter = painterResource(id = R.drawable.avatar),
               contentDescription = "avatar",
               modifier = Modifier.size(40.dp)
           )
           Spacer(modifier = Modifier.width(12.dp))

           Column {
               Text(
                   text = followModel.name,
                   fontWeight = FontWeight.Bold,
                   fontSize = 13.sp
               )
               Spacer(Modifier.height(5.dp))
               Text(
                   text = followModel.following,
                   color = Color.Gray,
                   fontSize = 11.sp
               )
           }

       }
       Button(
           onClick = { /* Apply logic */ },
           modifier = Modifier
               .width(90.dp)
               .height(30.dp),
           colors = ButtonDefaults.buttonColors(btnColor)
       ) {
           Text(text = btnText, fontSize = 12.sp, color = textColor)

       }
   }
}