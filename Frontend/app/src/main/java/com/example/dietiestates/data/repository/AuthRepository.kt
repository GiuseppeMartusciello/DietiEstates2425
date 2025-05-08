package com.example.dietiestates.data.repository

import android.util.Log
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.LoginRequest
import com.example.dietiestates.data.model.LoginResponse
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.remote.api.AuthApi
import com.example.dietiestates.utility.TokenManager
import okhttp3.Response

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response  = authApi.login(request)

            if (response.isSuccessful) {
                val token = response.body()?.accessToken
                if (!token.isNullOrBlank()) {
                    tokenManager.saveToken(token)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Token mancante nella risposta"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Errore login: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
