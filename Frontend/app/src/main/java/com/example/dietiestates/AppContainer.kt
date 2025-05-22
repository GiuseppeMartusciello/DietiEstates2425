package com.example.dietiestates


import android.content.Context
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.repository.AgencyRepository
import com.example.dietiestates.data.repository.AuthRepository
import com.example.dietiestates.data.repository.ListingRepository
import com.example.dietiestates.utility.TokenManager

object AppContainer {

    private var initialized = false

    lateinit var tokenManager: TokenManager
        private set

    lateinit var listingRepository: ListingRepository
        private set
    lateinit var agencyRepository: AgencyRepository
        private set

    lateinit var authRepository: AuthRepository
        private set


    fun init(context: Context) {
        if (initialized) return

        tokenManager = TokenManager(context.applicationContext)

        val retrofit = RetrofitClient.create(tokenManager)

        listingRepository = ListingRepository(retrofit.createListingApi())
        agencyRepository = AgencyRepository(retrofit.createAgencyApi())
        authRepository = AuthRepository(retrofit.createAuthApi(), tokenManager)

        initialized = true
    }
}
