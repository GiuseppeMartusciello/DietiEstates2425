package com.example.dietiestates.data.repository

import android.util.Log
import com.example.dietiestates.data.model.ClientsOffer
import com.example.dietiestates.data.model.CreateExternalOfferDto
import com.example.dietiestates.data.model.CreateOfferDto
import com.example.dietiestates.data.model.Guest
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.OfferStatusDto
import com.example.dietiestates.data.model.PropertyOffer
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.api.OfferApi
import org.json.JSONObject

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


    suspend fun  getOffersByListingClient(listingId: String): List<PropertyOffer> {

            val response = offerApi.getOffersByListingClient(listingId)

            if (response.isSuccessful) {
                val listings = response.body() ?: emptyList()
               return listings
            } else {
                throw Exception("Errore HTTP: ${response.code()}")
            }

    }
    suspend fun  getOffersByListingAgent(listingId: String, clientId: String): List<PropertyOffer> {

        val response = offerApi.getOffersByListingAgent(listingId, clientId)

        if (response.isSuccessful) {
            val listings = response.body() ?: emptyList()
            return listings
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }

    }

    suspend fun  getClientByListingAgent(listingId: String): List<ClientsOffer> {


        val response = offerApi.getClientByListingAgent(listingId)

        if (response.isSuccessful) {
            val listings = response.body() ?: emptyList()
            return listings
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }

    }
    suspend fun postOfferClient(listingId: String, price: Double): PropertyOffer {
        val response = offerApi.postOfferClient(listingId, CreateOfferDto(price))


        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Risposta vuota")
        } else {
            if (response.code() == 409)
                throw Exception("Impossibile inserire offerta perche' ne e' stata gia accettata un'altra")
            else
                throw Exception(" ${response.message()}")
        }
    }
    suspend fun postOfferAgent(listingId: String, clientId: String, price: Double): PropertyOffer {
        val response = offerApi.postOfferAgent(listingId, clientId ,CreateOfferDto(price))


        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Risposta vuota")
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                "Errore HTTP: ${response.code()}"
            }
            throw Exception(message)
        }
    }

    suspend fun postOfferExternal(listingId: String, guest: Guest, price: Double): PropertyOffer {
        val response = offerApi.postOfferExternal(listingId, CreateExternalOfferDto(guest.guestEmail, guest.guestName, guest.guestSurname, price))


        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Risposta vuota")
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                "Errore HTTP: ${response.code()}"
            }
            throw Exception(message)
        }
    }

    suspend fun getExternalOffers(listingId: String): List<ClientsOffer> {
        val response = offerApi.getExternalOffers(listingId)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Errore HTTP: ${response.code()}")
        }
    }




    suspend fun updateOfferState(offerId: String, status: String): PropertyOffer {
        val response = offerApi.updateOfferState(offerId, OfferStatusDto(status))
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            if (response.code() == 409)
                throw Exception("Impossibile accettare offerta perche' ne e' stata gia accettata un'altra")
            else
                throw Exception(" ${response.message()}")
        }
    }

}