package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ListingApi {
    @GET("listing")
    suspend fun getListings(): Response<List<Listing>>

    @GET("listing/{id}")
    suspend fun getListing(@Path("id") id: String): Response<Listing>

    @GET("listing/{id}/agent")
    suspend fun getAgentOfListing(@Path("id") id: String): Response<Agent>

    @GET("listing/all-images")
    suspend fun getAllListingImages(): Response<Map<String, List<String>>>

    @Multipart
    @POST("listing/{id}/images")
    suspend fun uploadListingImages(
        @Path("id") listingId: String,
        @Part images: List<MultipartBody.Part>
    ): Response<Unit>

    @DELETE("listing/{id}/images/{filename}")
    suspend fun deleteImageFromListing(
        @Path("id") listingId: String,
        @Path("filename") filename: String
    ): Response<Unit>


    @PATCH("listing/{id}")
    suspend fun modifyListing(
        @Path("id") listingId: String,
        @Body listingDto: ModifyOrCreateListingDto
    ): Response<Listing>

    @POST("listing/")
    suspend fun postListing(
        @Body listingDto: ModifyOrCreateListingDto
    ): Response<Listing>

    @DELETE("listing/{id}")
    suspend fun deleteListing(
        @Path("id") listingId: String,
    ): Response<Unit>

}
