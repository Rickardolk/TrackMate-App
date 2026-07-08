package com.trackmate.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class NominatimResult(
    val lat: String,
    val lon: String,
    val display_name: String
)

interface NominatimApi {
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): List<NominatimResult>
}