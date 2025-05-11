package com.example.dietiestates.data.repository

import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.remote.api.ListingApi

class ListingRepository(private val api: ListingApi) {
    private val BASE_IMAGE_URL = "http://10.0.2.2:3000"

    //Quando carico tutti i listing carico una sola foto per ogni listing,
    // ma facendo un'unica chiamata per tutte le immagini, per evitare il doppio delle chiamate per ogni listing
    suspend fun getListings(): List<Listing> {
        val listingsResponse = api.getListings()
        val imagesResponse = api.getAllListingImages()

        if (listingsResponse.isSuccessful /*&& imagesResponse.isSuccessful*/) {
            val baseListings = listingsResponse.body() ?: emptyList()
            val imageMap = imagesResponse.body() ?: emptyMap()

            return baseListings.map { listing ->
                val firstImage = imageMap[listing.id]?.firstOrNull()
                val fullUrl = firstImage?.let { "$BASE_IMAGE_URL$it" }
                listing.copy(imageUrls = listOfNotNull(fullUrl))
            }
        } else {
            val code = if (!listingsResponse.isSuccessful) listingsResponse.code() else imagesResponse.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }

    }

    suspend fun getListing(id: String): Listing? {
        val response = api.getListing(id)

        if (response.isSuccessful) {
            return response.body() ?: null
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }
    }

    suspend fun getListingImages(id: String): List<String> {

        val response = api.getListingImages(id)
        if (response.isSuccessful) {
            return response.body()?.map { "$BASE_IMAGE_URL$it" } ?: emptyList()
        } else {
            throw Exception("Errore nel caricamento immagini: ${response.code()}")
        }
    }
}
