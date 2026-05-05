package com.ojiambo.shambashare.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MpesaRequest(
    val phone: String,
    val amount: Int,
    val equipmentId: String,
    val renterUid: String
)

data class MpesaResponse(
    val success: Boolean,
    val paymentId: String,
    val checkoutRequestId: String
)

interface MpesaApi {
    @POST("stkPush")
    suspend fun stkPush(@Body request: MpesaRequest): MpesaResponse
}

object MpesaClient {
    private const val BASE_URL =
        "https://YOUR_PROJECT_ID.cloudfunctions.net/"

    val api: MpesaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MpesaApi::class.java)
    }
}

