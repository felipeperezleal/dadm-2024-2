package com.dadm.reto_10

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PuntosPosconsumoService {
    @GET("v29b-znjj.json")
    suspend fun getPuntosPosconsumo(): List<PuntoPosconsumo>
}

object RetrofitClient {
    private const val BASE_URL = "https://www.datos.gov.co/resource/"

    val instance: PuntosPosconsumoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PuntosPosconsumoService::class.java)
    }
}