package com.example.dietiestates.data.repository

import android.util.Log
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.PropertyOffer
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.OfferApi

class OfferRepository (private val offerApi: OfferApi, private val listingApi: ListingApi){

    suspend fun getListingsByUser(): Result<List<Listing>> {
        return try {
            val response = offerApi.getMyOfferListings()

            Log.d("DEBUG", "Chiamata terminata dalla repository, response: ${response.body()}")

            if (response.isSuccessful) {
                val listings = response.body() ?: emptyList()
                Result.success(listings)
            } else {
                Result.failure(Exception("Errore HTTP: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
        }


    suspend fun  getOffersByListing(listingId: String): List<PropertyOffer> {
            val response = offerApi.getOffersByListing(listingId)

            if (response.isSuccessful) {
                val listings = response.body() ?: emptyList()
               return listings
            } else {
                throw Exception("Errore HTTP: ${response.code()}")
            }

    }
}