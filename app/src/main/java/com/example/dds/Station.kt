package com.example.dds

import kotlinx.serialization.Serializable

@Serializable
data class Station(val name : String, val id : Int, val lat : String, val lng : String, val createdAt: String, val updatedAt: String)

@Serializable
data class StationsResponse (val data : List<Station>)