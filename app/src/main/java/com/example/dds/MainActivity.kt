package com.example.dds

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var stations: List<Station>
    var activeMission: Mission? = null
    lateinit var ddsApi: DdsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3001/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ddsApi = retrofit.create(DdsApi::class.java)

        val startMissionBtn = findViewById<Button>(R.id.start_mission_btn)
        val ackLoadBtn = findViewById<Button>(R.id.ack_load_btn)
        val ackRecvBtn = findViewById<Button>(R.id.ack_receive_btn)

        ackLoadBtn.setOnClickListener {
            ackLoad()
        }

        ackRecvBtn.setOnClickListener {
            ackReceive()
        }

        startMissionBtn.setOnClickListener {
            val fromStationSpinner = findViewById<Spinner>(R.id.from_station_spinner)
            val toStationSpinner = findViewById<Spinner>(R.id.to_station_spinner)

            val fromStationIndex = fromStationSpinner.selectedItemPosition
            val toStationIndex = toStationSpinner.selectedItemPosition

            val fromStation = stations[fromStationIndex]
            val toStation = stations[toStationIndex]

            createMission(fromStation, toStation)
        }

        fetchStations()
        fetchActiveMission()
    }

    fun ackLoad() {
        if(activeMission == null)
            return

        ddsApi.ackPackageLoad()?.enqueue(object : Callback<Object> {
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

        ddsApi.ackPackageReceive()?.enqueue(object : Callback<Object> {
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
        ddsApi.createMission(CreateMissionRequest(fromStation.id, toStation.id))
            ?.enqueue(object : Callback<Object> {
                override fun onResponse(call: Call<Object>, response: Response<Object>) {
                    if (response.code() == 200) {
                        fetchActiveMission()
                        Toast.makeText(
                            applicationContext,
                            "Mission Started Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Failed to start mission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Object>, t: Throwable) {

                }

            })
    }

    fun fetchStations() {
        ddsApi.listStations()?.enqueue(object : Callback<StationsResponse> {
            override fun onResponse(
                call: Call<StationsResponse>,
                response: Response<StationsResponse>
            ) {
                stations = response.body()?.data!!
                populateStations()
            }

            override fun onFailure(call: Call<StationsResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun fetchActiveMission() {
        ddsApi.getActiveMission()?.enqueue(object : Callback<Mission> {
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
        val activeMissionTv = findViewById<TextView>(R.id.cur_mission_tv)

        if (activeMission!!.id == null) {
            activeMissionTv.visibility = View.GONE
        }else {
            activeMissionTv.visibility = View.VISIBLE
            activeMissionTv.text =
                "Current Mission: " + activeMission!!.id + " (" + activeMission!!.mission_status + ")"

        }

        val startMissionBtn = findViewById<Button>(R.id.start_mission_btn)
        val ackLoadBtn = findViewById<Button>(R.id.ack_load_btn)
        val ackRecvBtn = findViewById<Button>(R.id.ack_receive_btn)

        startMissionBtn.isClickable = activeMission?.id == null
        ackLoadBtn.isClickable = activeMission != null && activeMission?.mission_status == "awaiting_load"
        ackRecvBtn.isClickable = activeMission != null && activeMission?.mission_status == "awaiting_unload"
    }

    fun populateStations() {
        val fromStationSpinner = findViewById<Spinner>(R.id.from_station_spinner)
        val toStationSpinner = findViewById<Spinner>(R.id.to_station_spinner)

        val arrayAdapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)

        stations.forEach {
            arrayAdapter.add("Station " + it.id + " - " + it.name)
        }

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromStationSpinner.adapter = arrayAdapter
        toStationSpinner.adapter = arrayAdapter
    }
}