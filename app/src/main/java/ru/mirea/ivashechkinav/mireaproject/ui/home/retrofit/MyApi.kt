package ru.mirea.ivashechkinav.mireaproject.ui.home.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface MyApi {
    @GET("Time/current/zone?timeZone=Europe/Amsterdam")
    suspend fun testApi(): ApiResponse
}