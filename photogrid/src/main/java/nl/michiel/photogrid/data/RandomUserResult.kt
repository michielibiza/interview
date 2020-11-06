package nl.michiel.photogrid.data

data class RandomUserResult(
    val results: List<RandomUser>
)

class RandomUser(
    val picture: RandomUserPicture
)

class RandomUserPicture(
    val large: String
)
