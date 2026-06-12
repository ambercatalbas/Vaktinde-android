package com.ambercatalbas.vaktinde.core.data.remote.diyanet

import retrofit2.http.GET
import retrofit2.http.Path

interface DiyanetApiService {

    @GET("api/prayer-times/{locationId}/range")
    suspend fun getMonthlyTimes(
        @Path("locationId") locationId: String,
    ): DiyanetResponse

    @GET("api/location/search/{query}")
    suspend fun searchLocation(
        @Path("query") query: String,
    ): DiyanetLocationResponse
}
