package nl.michiel.photogrid.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RandomUserService {
    @GET("api/?results=500")
    suspend fun loadUserList(): RandomUserResult
}

fun createService(): RandomUserService {

    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    return Retrofit.Builder().baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(RandomUserService::class.java)
}
