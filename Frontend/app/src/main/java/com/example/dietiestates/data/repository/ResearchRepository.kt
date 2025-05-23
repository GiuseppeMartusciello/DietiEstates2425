package com.example.dietiestates.data.repository

import com.example.dietiestates.data.model.CreateResearchDto
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.ResearchApi

data class ResearchRepository(private val api: ResearchApi, private val apiListing: ListingApi) {
    private val BASE_IMAGE_URL = "http://10.0.2.2:3000"

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
                val fullUrl = firstImage?.let { "$BASE_IMAGE_URL$it" }
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

    suspend fun updateResearch(researchId: String): Research {
        val response = api.updateResearch(researchId)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Nessun corpo nella risposta")
        } else {
            throw Exception("Errore ${response.code()}: ${response.errorBody()?.string()}")
        }
    }




}
