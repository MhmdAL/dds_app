package com.example.ecarrier

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.*

interface ECarrierApi {
    @GET("station")
    fun listStations(): Call<StationsResponse>?

    @POST("start_mission")
    fun createMission(@Header("Authorization") token: String, @Body request: CreateMissionRequest): Call<Object>?

    @POST("ack_package_loaded")
    fun ackPackageLoad(): Call<Object>?

    @POST("ack_package_received")
    fun ackPackageReceive(): Call<Object>?

    @GET("mission")
    fun getActiveMission(@Header("Authorization") token: String): Call<Mission>?

    @POST("user")
    fun createUser(@Header("Authorization") token: String, @Body request: CreateUserRequest): Call<User>?
}

@Serializable
data class CreateUserRequest(val name: String?, val registrationToken: String)

@Serializable
data class CreateMissionRequest(val source_station_id: Int, val dest_station_id: Int, val recepient_id: Int)

@Serializable
data class Mission(
    val id: Int?,
    val drone_id: Int,
    val source_station_id: Int,
    val dest_station_id: Int,
    val current_lat: String,
    val current_lng: String,
    val sender_id: Int,
    val recepient_id: Int,
    val mission_status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class User(val id: Int)

@Serializable
data class Station(val name : String, val id : Int, val lat : String, val lng : String, val createdAt: String, val updatedAt: String)

@Serializable
data class StationsResponse (val data : List<Station>)