package com.example.dds

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DdsApi {
    @GET("station")
    fun listStations(): Call<StationsResponse>?

    @POST("start_mission")
    fun createMission(@Body request: CreateMissionRequest): Call<Object>?

    @POST("ack_package_loaded")
    fun ackPackageLoad(): Call<Object>?

    @POST("ack_package_received")
    fun ackPackageReceive(): Call<Object>?

    @GET("mission")
    fun getActiveMission(): Call<Mission>?
}

@Serializable
data class CreateMissionRequest(val source_station_id: Int, val dest_station_id: Int)

@Serializable
data class Mission(
    val id: Int?,
    val drone_id: Int,
    val source_station_id: Int,
    val dest_station_id: Int,
    val current_lat: String,
    val current_lng: String,
    val mission_status: String,
    val createdAt: String,
    val updatedAt: String
)