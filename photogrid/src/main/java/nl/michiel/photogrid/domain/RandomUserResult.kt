package nl.michiel.photogrid.domain

data class RandomUserResult(
    val results: List<RandomUser>
)

class RandomUser(
    val picture: RandomUserPicture
)

class RandomUserPicture(
    val large: String
)
