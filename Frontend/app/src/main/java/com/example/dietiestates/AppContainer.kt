package com.example.dietiestates


import android.content.Context
import android.util.Log
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.repository.AgencyRepository
import com.example.dietiestates.data.repository.AuthRepository
import com.example.dietiestates.data.repository.ClientRepository
import com.example.dietiestates.data.repository.ListingRepository
import com.example.dietiestates.data.repository.NotificationRepository
import com.example.dietiestates.data.repository.ResearchRepository
import com.example.dietiestates.data.repository.OfferRepository
import com.example.dietiestates.utility.TokenManager

object AppContainer {

    private var initialized = false

    lateinit var clientRepository: ClientRepository
        private set

    lateinit var listingRepository: ListingRepository
        private set

    lateinit var agencyRepository: AgencyRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var researchRepository: ResearchRepository
        private set

    lateinit var  offerRepository: OfferRepository
        private  set

    lateinit var notificationRepository: NotificationRepository
        private set

    fun reInit(context: Context) {
        //tokenManager = TokenManager(context.applicationContext)
        TokenManager.init(context)

        val retrofit = RetrofitClient.create(TokenManager)
        clientRepository = ClientRepository(retrofit.createClientApi())
        listingRepository = ListingRepository(retrofit.createListingApi())
        agencyRepository = AgencyRepository(retrofit.createAgencyApi())
        authRepository = AuthRepository(retrofit.createAuthApi())
        offerRepository = OfferRepository(retrofit.createOfferApi(), retrofit.createListingApi())
        notificationRepository = NotificationRepository(retrofit.createNotificationApi())
        researchRepository = ResearchRepository(retrofit.createResearchApi(), retrofit.createListingApi())

        initialized = true
    }


    fun init(context: Context) {
        if (!initialized) {
            reInit(context)
        }
    }
}


