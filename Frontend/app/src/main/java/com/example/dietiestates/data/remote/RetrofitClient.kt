package com.example.dietiestates.data.remote

import com.example.dietiestates.data.remote.api.AuthApi
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.OfferApi
import com.example.dietiestates.utility.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(retrofit: Retrofit) {

    private val listingApi = retrofit.create(ListingApi::class.java)
    private val authApi = retrofit.create(AuthApi::class.java)
    private val offerApi = retrofit.create(OfferApi::class.java)

    fun createListingApi() = listingApi
    fun createAuthApi() = authApi

    fun createOfferApi () = offerApi

    companion object {
        fun create(tokenManager: TokenManager): RetrofitClient {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

            val retrofit = Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.1.13:3000/")
                .baseUrl("http://dietiestates.duckdns.org:3000")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return RetrofitClient(retrofit)
        }
    }
}