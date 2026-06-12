package com.ambercatalbas.vaktinde.core.data.remote.aladhan

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AladhanApiService {

    @GET("v1/calendar/{year}/{month}")
    suspend fun getMonthlyTimes(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
    ): AladhanResponse
}
