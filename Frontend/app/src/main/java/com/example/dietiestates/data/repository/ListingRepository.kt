package com.example.dietiestates.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.CreateListingDto
import com.example.dietiestates.data.model.dto.ModifyListingDto
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.utility.ApiConstants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ListingRepository(private val api: ListingApi) {

    suspend fun getListings(): List<Listing> {

        val listingsResponse = api.getListings()

        if (listingsResponse.isSuccessful ) {
            val listing = listingsResponse.body()?.map { listing ->
                listing.copy(
                    imageUrls = listing.imageUrls.map { "${ApiConstants.BASE_URL}$it" }
                )
            } ?: emptyList()

            return listing
        } else {
            val code = listingsResponse.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }

    }

    suspend fun getListing(id: String): Listing? {
        val response = api.getListing(id)

        if (response.isSuccessful) {
            val listing = response.body() ?: null
            return listing?.copy(
                imageUrls = listing.imageUrls.map { "${ApiConstants.BASE_URL}$it" }
            )
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }
    }

    suspend fun getAgentOfListing(id: String): Agent? {
        val response = api.getAgentOfListing(id)

        if (response.isSuccessful) {
            val agent = response.body() ?: null

            return agent
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }
    }

    suspend fun uploadImagesToListing(
        context: Context,
        listingId: String,
        uris: List<Uri>
    ) {
        val contentResolver = context.contentResolver

        val parts = uris.mapIndexed { index, uri ->
            val inputStream = contentResolver.openInputStream(uri)!!
            val fileBytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                name = "images",
                filename = "photo_$index.jpg",
                body = requestBody
            )
        }

        try {
            val response = api.uploadListingImages(listingId, parts)
            if (response.isSuccessful) {
                Log.d("UPLOAD", "Immagini caricate con successo")
            } else {
                Log.e("UPLOAD", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("UPLOAD", "Errore durante l'upload", e)
        }
    }


    suspend fun deleteImageFromListing(listingId: String, filename: String) {
        val response = api.deleteImageFromListing(listingId, filename)
        if (!response.isSuccessful) {
            throw Exception("Errore durante l'eliminazione: ${response.code()}")
        }
    }

    suspend fun deleteListing(listingId: String) {
        val response = api.deleteListing(listingId)
        if (!response.isSuccessful) {
            throw Exception("Errore durante l'eliminazione: ${response.code()}")
        }
    }
    suspend fun modifyListing(listingId: String, dto: ModifyListingDto): Listing {
        val response = api.modifyListing(listingId, dto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Nessun corpo nella risposta")
        } else {
            throw Exception("Errore ${response.code()}: ${response.errorBody()?.string()}")
        }
    }


    suspend fun postListing(dto: CreateListingDto): Listing {
        val response = api.postListing(dto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Nessun corpo nella risposta")
        } else {
            throw Exception("Errore ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
