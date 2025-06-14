package com.example.dietiestates.data.repository

import android.util.Log
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.AuthResult
import com.example.dietiestates.data.model.ChangePasswordRequest
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.LoginRequest
import com.example.dietiestates.data.model.LoginResponse
import com.example.dietiestates.data.model.SignUpRequest
import com.example.dietiestates.data.remote.api.ListingApi
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.remote.api.AuthApi
import com.example.dietiestates.utility.TokenManager
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager = TokenManager
) {

    suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response  = authApi.login(request)

            if (response.isSuccessful) {
                val token = response.body()?.accessToken
                val mustChange = response.body()?.mustChangePassword?: false

                if (!token.isNullOrBlank()) {
                    tokenManager.saveToken(token)
                    Result.success(AuthResult(success = true, mustChangePassword = mustChange))
                } else {
                    Result.failure(Exception("Token mancante nella risposta"))
                }
            } else {
                var errorBody = response.errorBody()?.string()
                var errorMessage: String
                var errorCode: String?
                var errorJson = JSONObject()
                var provider: String? = null

                try {
                    errorJson = JSONObject(errorBody ?: "{}")

                    // Gestione del campo "message": può essere array o stringa
                    val rawMessage = errorJson.opt("message")
                    errorMessage = when (rawMessage) {
                        is String -> rawMessage
                        is JSONArray -> {
                            if (rawMessage.length() > 0) rawMessage.getString(0) else "Richiesta non valida"
                        }
                        else -> "Errore sconosciuto"
                    }

                    errorCode = errorJson.optString("error", "")
                    provider = errorJson.optString("provider", null)
                } catch (e: JSONException) {
                    errorMessage = "Errore sconosciuto"
                    errorCode = null
                }

                val finalMessage = when (response.code()) {
                    401 -> "Credenziali errate."
                    400 -> when (errorCode) {
                        "ProviderMismatch" -> {
                            val readableProvider = when (provider?.uppercase()) {
                                "GOOGLE" -> "Google"
                                else -> provider ?: "un altro provider"
                            }
                            "Hai registrato l'account con $readableProvider. Per favore accedi con $readableProvider."
                        }
                        else -> when {
                            errorMessage.contains("must be an email", ignoreCase = true) ->
                                "Inserisci un'email valida."
                            errorMessage.contains("should not be empty", ignoreCase = true) ->
                                "Tutti i campi sono obbligatori."
                            else -> "Errore nei dati inseriti."
                        }
                    }
                    else -> "Errore sconosciuto"
                }

                return Result.failure(Exception(finalMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            val response = authApi.loginWithGoogleToken(mapOf("idToken" to idToken))
            tokenManager.saveToken(response.accessToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Errore nella chiamata a /google-token", e)
            Result.failure(e)
        }
    }


    suspend fun signUp(request: SignUpRequest): Result<Unit> {
        return try {
            val response = authApi.signUp(request)
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
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    when (val message = json["message"]) {
                        is String -> message
                        is org.json.JSONArray -> {
                            val messages = (0 until message.length()).map { message.getString(it) }
                            messages.joinToString("\n")
                        }
                        else -> "Errore durante la registrazione"
                    }
                } catch (e: Exception) {
                    "Errore sconosciuto durante la registrazione"
                }
                Result.failure(Exception(errorMessage))

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val request = ChangePasswordRequest(currentPassword, newPassword)
            val response = authApi.changePassword(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    when (val message = json["message"]) {
                        is String -> message
                        is org.json.JSONArray -> {
                            val messages = (0 until message.length()).map { message.getString(it) }
                            messages.joinToString("\n")
                        }
                        else -> "Errore durante il cambio password"
                    }
                } catch (e: Exception) {
                    "Errore sconosciuto durante il cambio password"
                }
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
