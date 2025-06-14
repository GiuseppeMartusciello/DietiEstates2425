package com.example.dietiestates.data.repository

import com.example.dietiestates.data.model.dto.CreateResearchDto
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.ResearchApi
import com.example.dietiestates.utility.ApiConstants

data class ResearchRepository(private val api: ResearchApi, private val apiListing: ListingApi) {

    suspend fun getLast10Researches(): List<Research> {
        val response = api.getLast10Researches()

        if(response.isSuccessful){
            val body = response.body() ?: emptyList()
            return body
        }
        else{
            val code = if (!response.isSuccessful) response.code() else response.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun createResearch(researchdto: CreateResearchDto): List<Listing> {
        val response = api.createResearch(researchdto)
        val imagesResponse = apiListing.getAllListingImages()

        if (response.isSuccessful) {
            val body = response.body() ?: emptyList()
            val imageMap = imagesResponse.body() ?: emptyMap()

            return body.map { listing ->
                val firstImage = imageMap[listing.id]?.firstOrNull()
                val fullUrl = firstImage?.let { "${ApiConstants.BASE_URL}$it" }
                listing.copy(imageUrls = listOfNotNull(fullUrl))
            }
        } else {
            val code = if (!response.isSuccessful) response.code() else imagesResponse.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }


    suspend fun deleteResearch(researchId: String) {
        val response = api.deleteResearch(researchId)
        if (!response.isSuccessful) {
            throw Exception("Errore durante l'eliminazione: ${response.code()}")
        }
    }

    suspend fun updateResearch(researchId: String): List<Listing> {
        val response = api.updateResearch(researchId)
        val imagesResponse = apiListing.getAllListingImages()

        if (response.isSuccessful) {
            val body = response.body() ?: emptyList()
            val imageMap = imagesResponse.body() ?: emptyMap()

            return body.map { listing ->
                val firstImage = imageMap[listing.id]?.firstOrNull()
                val fullUrl = firstImage?.let { "${ApiConstants.BASE_URL}$it" }
                listing.copy(imageUrls = listOfNotNull(fullUrl))
            }
        } else {
            val code = if (!response.isSuccessful) response.code() else imagesResponse.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }
}
