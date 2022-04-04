package com.example.ecarrier

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateMissionFragment : Fragment(R.layout.fragment_create_mission) {
    lateinit var stations: List<Station>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startMissionBtn = view?.findViewById<Button>(R.id.start_mission_btn)

        startMissionBtn?.setOnClickListener {
            val recipientIdEt = view?.findViewById<EditText>(R.id.recipientIdET)
            val fromStationSpinner = view?.findViewById<Spinner>(R.id.from_station_spinner)
            val toStationSpinner = view?.findViewById<Spinner>(R.id.to_station_spinner)

            val fromStationIndex = fromStationSpinner?.selectedItemPosition
            val toStationIndex = toStationSpinner?.selectedItemPosition

            val fromStation = stations[fromStationIndex!!]
            val toStation = stations[toStationIndex!!]

            if (recipientIdEt.text.isNullOrEmpty()) {
                recipientIdEt.error = "Recipient Id not specified."

                return@setOnClickListener
            }

            if(recipientIdEt.text.toString().toIntOrNull() == null) {
                recipientIdEt.error = "Recipient Id must be an integer."

                return@setOnClickListener
            }

            val recipientId = recipientIdEt.text.toString().toInt()

            createMission(fromStation, toStation, recipientId)
        }

        fetchStations()
    }

    fun createMission(fromStation: Station, toStation: Station, recipientId: Int) {
        lifecycleScope.launch {
            HttpClient.eCarrierApi.createMission("Bearer " + UserState.userToken, CreateMissionRequest(fromStation.id, toStation.id, recipientId))
                ?.enqueue(object : Callback<Object> {
                    override fun onResponse(call: Call<Object>, response: Response<Object>) {
                        if (response.code() == 200) {
                            Toast.makeText(
                                context,
                                "Mission Started Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to start mission",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        findNavController().navigate(R.id.action_createMissionFragment_to_trackMissionFragment)
                    }

                    override fun onFailure(call: Call<Object>, t: Throwable) {

                    }
                })
        }
    }

    fun fetchStations() {
        HttpClient.eCarrierApi.listStations()?.enqueue(object : Callback<StationsResponse> {
            override fun onResponse(
                call: Call<StationsResponse>,
                response: Response<StationsResponse>
            ) {
                stations = response.body()?.data!!
                populateStations()
            }

            override fun onFailure(call: Call<StationsResponse>, t: Throwable) {

            }
        })
    }

    fun populateStations() {
        val fromStationSpinner = view?.findViewById<Spinner>(R.id.from_station_spinner)
        val toStationSpinner = view?.findViewById<Spinner>(R.id.to_station_spinner)

        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item)

        stations.forEach {
            arrayAdapter.add("Station " + it.id + " - " + it.name)
        }

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromStationSpinner?.adapter = arrayAdapter
        toStationSpinner?.adapter = arrayAdapter
    }
}