data class Follower(
    val _id: String,
    val name: String,
    val avatar: String
)

data class GetFollowerResponse(
    val followers: List<Follower>,
    val count: Int,
    val message: String
)