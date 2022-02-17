package com.example.dds

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateMissionFragment : Fragment() {
    lateinit var stations: List<Station>
    var activeMission: Mission? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startMissionBtn = view?.findViewById<Button>(R.id.start_mission_btn)
        val ackLoadBtn = view?.findViewById<Button>(R.id.ack_load_btn)
        val ackRecvBtn = view?.findViewById<Button>(R.id.ack_receive_btn)

        ackLoadBtn?.setOnClickListener {
            ackLoad()
        }

        ackRecvBtn?.setOnClickListener {
            ackReceive()
        }

        startMissionBtn?.setOnClickListener {
            val fromStationSpinner = view?.findViewById<Spinner>(R.id.from_station_spinner)
            val toStationSpinner = view?.findViewById<Spinner>(R.id.to_station_spinner)

            val fromStationIndex = fromStationSpinner?.selectedItemPosition
            val toStationIndex = toStationSpinner?.selectedItemPosition

            val fromStation = stations[fromStationIndex!!]
            val toStation = stations[toStationIndex!!]

            createMission(fromStation, toStation)
        }

        fetchStations()
        fetchActiveMission()
    }

    fun ackLoad() {
        if(activeMission == null)
            return

        HttpClient.ddsApi.ackPackageLoad()?.enqueue(object : Callback<Object> {
            override fun onResponse(
                call: Call<Object>,
                response: Response<Object>
            ) {
                fetchActiveMission()
            }

            override fun onFailure(call: Call<Object>, t: Throwable) {

            }
        })
    }

    fun ackReceive() {
        if(activeMission == null)
            return

        HttpClient.ddsApi.ackPackageReceive()?.enqueue(object : Callback<Object> {
            override fun onResponse(
                call: Call<Object>,
                response: Response<Object>
            ) {
                Log.e("test", "acking recv")
                fetchActiveMission()
            }

            override fun onFailure(call: Call<Object>, t: Throwable) {
                Log.e("test", "acking recv failed")
            }
        })
    }

    fun createMission(fromStation: Station, toStation: Station) {
        HttpClient.ddsApi.createMission(CreateMissionRequest(fromStation.id, toStation.id))
            ?.enqueue(object : Callback<Object> {
                override fun onResponse(call: Call<Object>, response: Response<Object>) {
                    if (response.code() == 200) {
                        fetchActiveMission()
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

    fun fetchStations() {
        HttpClient.ddsApi.listStations()?.enqueue(object : Callback<StationsResponse> {
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

    fun fetchActiveMission() {
        HttpClient.ddsApi.getActiveMission()?.enqueue(object : Callback<Mission> {
            override fun onResponse(
                call: Call<Mission>,
                response: Response<Mission>
            ) {
                activeMission = response.body()
                Log.e("test", activeMission.toString())
                updateActiveMission()
            }

            override fun onFailure(call: Call<Mission>, t: Throwable) {
                activeMission = null
                updateActiveMission()
            }
        })
    }

    fun updateActiveMission() {
        val activeMissionTv = view?.findViewById<TextView>(R.id.cur_mission_tv)

        if (activeMission?.id == null) {
            activeMissionTv?.visibility = View.GONE
        }else {
            activeMissionTv?.visibility = View.VISIBLE
            activeMissionTv?.text =
                "Current Mission: " + activeMission!!.id + " (" + activeMission!!.mission_status + ")"

        }

        val startMissionBtn = view?.findViewById<Button>(R.id.start_mission_btn)
        val ackLoadBtn = view?.findViewById<Button>(R.id.ack_load_btn)
        val ackRecvBtn = view?.findViewById<Button>(R.id.ack_receive_btn)

        startMissionBtn?.isClickable = activeMission?.id == null
        ackLoadBtn?.isClickable = activeMission != null && activeMission?.mission_status == "awaiting_load"
        ackRecvBtn?.isClickable = activeMission != null && activeMission?.mission_status == "awaiting_unload"
    }

    fun populateStations() {
        val fromStationSpinner = view?.findViewById<Spinner>(R.id.from_station_spinner)
        val toStationSpinner = view?.findViewById<Spinner>(R.id.to_station_spinner)

        val arrayAdapter =
            ArrayAdapter<String>(context!!, R.layout.support_simple_spinner_dropdown_item)

        stations.forEach {
            arrayAdapter.add("Station " + it.id + " - " + it.name)
        }

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromStationSpinner?.adapter = arrayAdapter
        toStationSpinner?.adapter = arrayAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_mission, container, false)
    }
}