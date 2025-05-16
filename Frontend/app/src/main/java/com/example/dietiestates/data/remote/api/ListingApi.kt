package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Listing
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ListingApi {
    @GET("listing")
    suspend fun getListings(): Response<List<Listing>>

    @GET("listing/{id}")
    suspend fun getListing(@Path("id") id: String): Response<Listing>

    @GET("listing/{id}/images")
    suspend fun getListingImages(@Path("id") id: String): Response<List<String>>

    @GET("listing/all-images")
    suspend fun getAllListingImages(): Response<Map<String, List<String>>>

}
