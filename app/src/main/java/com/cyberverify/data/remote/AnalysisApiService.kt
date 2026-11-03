package com.cyberverify.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AnalysisApiService {

    @Multipart
    @POST("api/v1/analyze/image")
    suspend fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("detectionDepth") depth: RequestBody? = null
    ): Response<AnalysisResponse>

    @Multipart
    @POST("api/v1/analyze/audio")
    suspend fun analyzeAudio(
        @Part file: MultipartBody.Part,
        @Part("detectionDepth") depth: RequestBody? = null
    ): Response<AnalysisResponse>

    @GET("api/v1/reports/{reportId}")
    suspend fun getReport(
        @Path("reportId") reportId: String
    ): Response<AnalysisResponse>
}
