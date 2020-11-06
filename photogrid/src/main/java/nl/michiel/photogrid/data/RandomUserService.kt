package nl.michiel.photogrid.data

import retrofit2.Retrofit
import retrofit2.http.GET

interface RandomUserService {
    @GET("api")
    suspend fun loadUserList(): RandomUserResult
}

fun createService(): RandomUserService =
    Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .build()
        .create(RandomUserService::class.java)