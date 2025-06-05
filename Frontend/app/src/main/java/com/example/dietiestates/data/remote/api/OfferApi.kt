package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.ClientsOffer
import com.example.dietiestates.data.model.CreateExternalOfferDto
import com.example.dietiestates.data.model.CreateOfferDto
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.OfferStatusDto
import com.example.dietiestates.data.model.PropertyOffer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OfferApi {

    @GET("offer/my-offer/listing")
    suspend fun getMyOfferListings(): Response<List<Listing>>


    @GET("offer/listing/{listingId}/offers")
    suspend fun getOffersByListingClient(
        @Path("listingId") listingId: String
    ): Response<List<PropertyOffer>>


    @GET("offer/listing/{listingId}/client/{clientId}/offers")
    suspend fun getOffersByListingAgent(
        @Path("listingId") listingId: String,
        @Path("clientId") clientId: String
    ): Response<List<PropertyOffer>>


    @GET("offer/listing/{listingId}/clients")
    suspend fun getClientByListingAgent(
        @Path("listingId") listingId: String
    ): Response<List<ClientsOffer>>

    @GET("offer/listing/{listingId}/external")
    suspend fun getExternalOffers(
        @Path("listingId") listingId: String
    ): Response<List<ClientsOffer>>



    @POST("offer/listing/{listingId}")
    suspend fun postOfferClient(
        @Path("listingId") listingId: String,
        @Body createOfferDto: CreateOfferDto
    ): Response<PropertyOffer>

    @POST("offer/listing/{listingId}/client/{clientId}")
    suspend fun postOfferAgent(
        @Path("listingId") listingId: String,
        @Path("clientId") clientId: String,
        @Body createOfferDto: CreateOfferDto
    ): Response<PropertyOffer>

    @POST("offer/listing/{listingId}/external")
    suspend fun postOfferExternal(
        @Path("listingId") listingId: String,
        @Body createOfferDto: CreateExternalOfferDto
    ): Response<PropertyOffer>


    @PATCH("offer/{id}")
    suspend fun updateOfferState(
        @Path("id") offerId: String,
        @Body status: OfferStatusDto // es. {"status": "ACCEPTED"}
    ): Response<PropertyOffer>


}