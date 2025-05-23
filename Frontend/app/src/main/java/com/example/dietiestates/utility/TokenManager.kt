package com.example.dietiestates.utility

import android.content.Context
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "auth"
        private const val TOKEN_KEY = "access_token"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        //decodifico il mio token
        val jwt = JWT(token)
        // prendo il ruolo dell'utente e lo salvo
        val role = jwt.getClaim("role").asString()

        prefs.edit()
            .putString(TOKEN_KEY, token)
            .putString("user_role", role)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        val token = getToken() ?: return false
        val jwt = JWT(token)
        return !jwt.isExpired(10)
    }


    fun getUserRole(): String? {
        return prefs.getString("user_role", null)
    }

}
