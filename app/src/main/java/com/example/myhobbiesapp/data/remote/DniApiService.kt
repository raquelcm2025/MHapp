package com.example.myhobbiesapp.data.remote

import com.example.myhobbiesapp.data.model.DniResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DniApiService {
    @GET("api/v1/dni/{dni}")
    suspend fun buscarDni(@Path("dni") dni: String): Response<DniResponse>
}
