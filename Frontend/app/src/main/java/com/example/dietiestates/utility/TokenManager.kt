package com.example.dietiestates.utility

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT

object TokenManager {
    private const val PREFS_NAME = "auth"
    private const val TOKEN_KEY = "access_token"
    private const val ROLE_KEY = "user_role"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        val jwt = JWT(token)
        val role = jwt.getClaim("role").asString()

        prefs.edit()
            .putString(TOKEN_KEY, token)
            .putString(ROLE_KEY, role)
            .commit() // ⬅️ usa commit per salvare subito
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearSession() {
        prefs.edit().clear().commit()
    }

    fun isLoggedIn(): Boolean {
        val token = getToken() ?: return false
        return !JWT(token).isExpired(10)
    }

    fun getUserRole(): String? {
        return prefs.getString(ROLE_KEY, null)
    }
}
