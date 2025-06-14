package com.example.dietiestates.data.remote

import android.util.Log
import com.example.dietiestates.data.remote.api.AgencyApi
import com.example.dietiestates.data.remote.api.AuthApi
import com.example.dietiestates.data.remote.api.ClientApi
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.NotificationApi
import com.example.dietiestates.data.remote.api.OfferApi
import com.example.dietiestates.data.remote.api.ResearchApi
import com.example.dietiestates.utility.ApiConstants
import com.example.dietiestates.utility.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(retrofit: Retrofit) {

    private val clientApi = retrofit.create(ClientApi::class.java)
    private val listingApi = retrofit.create(ListingApi::class.java)
    private val agencyApi = retrofit.create(AgencyApi::class.java)
    private val authApi = retrofit.create(AuthApi::class.java)
    private val offerApi = retrofit.create(OfferApi::class.java)
    private val researchApi = retrofit.create(ResearchApi::class.java)
    private val notificationApi = retrofit.create(NotificationApi::class.java)

    fun createClientApi() = clientApi
    fun createListingApi() = listingApi
    fun createAgencyApi() = agencyApi
    fun createAuthApi() = authApi
    fun createOfferApi () = offerApi
    fun createNotificationApi() = notificationApi
    fun createResearchApi() = researchApi

    companion object {
        fun create(tokenManager: TokenManager): RetrofitClient {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                    //todo leva i commenti e modifica manifest
                 //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.1.2:3000/")
                 .baseUrl("${ApiConstants.BASE_URL}/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return RetrofitClient(retrofit)
        }
    }
}