package com.example.dietiestates.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import com.example.dietiestates.data.remote.api.ListingApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ListingRepository(private val api: ListingApi) {
    private val BASE_IMAGE_URL = "http://dietiestates.duckdns.org:3000"

    suspend fun getListings(): List<Listing> {
        val listingsResponse = api.getListings()

        if (listingsResponse.isSuccessful ) {
            val listing = listingsResponse.body()?.map { listing ->
                listing.copy(
                    imageUrls = listing.imageUrls.map { "$BASE_IMAGE_URL$it" }
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
            Log.d("output",listing.toString())
            return listing?.copy(
                imageUrls = listing.imageUrls.map { "$BASE_IMAGE_URL$it" }
            )
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
                name = "images", // deve corrispondere a `@UploadedFiles()` su NestJS
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
    suspend fun modifyListing(listingId: String, dto: ModifyOrCreateListingDto): Listing {
        val response = api.modifyListing(listingId, dto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Nessun corpo nella risposta")
        } else {
            throw Exception("Errore ${response.code()}: ${response.errorBody()?.string()}")
        }
    }


    suspend fun postListing(dto: ModifyOrCreateListingDto): Listing {
        val response = api.postListing(dto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Nessun corpo nella risposta")
        } else {
            throw Exception("Errore ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
