package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.PropertyOffer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OfferApi {

        @GET("offer/my-offer/listing")
        suspend fun getMyOfferListings(): Response<List<Listing>>


        @GET("offer/listing/{listingId}/offers")
        suspend fun getOffersByListing(
               @Path("listingId") listingId: String
        ): Response <List<PropertyOffer>>

}