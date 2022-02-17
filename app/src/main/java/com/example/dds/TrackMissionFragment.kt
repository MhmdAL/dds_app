package com.example.dds

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration

class TrackMissionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_mission, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchActiveMission()

        val fab = view.findViewById<FloatingActionButton>(R.id.start_mission_fab)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_trackMissionFragment_to_createMissionFragment)
        }
    }

    fun fetchActiveMission() {
        lifecycleScope.launch(Dispatchers.IO) {
            val mission = HttpClient.ddsApi.getActiveMission()?.execute()?.body()

            withContext(Dispatchers.Main) {
                updateUiForMission(mission)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

//        WorkManager.getInstance(requireContext()).cancelUniqueWork("missionStatusUpdater")
    }

    fun updateUiForMission(mission: Mission?) {
        view?.let {
            val missionStatusTv = it.findViewById<TextView>(R.id.mission_status_tv)
            val missionStatusDescTv = it.findViewById<TextView>(R.id.mission_status_desc_tv)
            val missionStatusImg = it.findViewById<ImageView>(R.id.mission_status_img)
            val confirmBtn = it.findViewById<Button>(R.id.confirm_btn)
            val startMissionFab = it.findViewById<FloatingActionButton>(R.id.start_mission_fab)

            startMissionFab.visibility = if(mission?.id == null) View.VISIBLE else View.GONE

            when (mission?.mission_status) {
                "new_mission" -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text = resources.getString(R.string.mission_status_desc_heading_source)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "heading_source" -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text = resources.getString(R.string.mission_status_desc_heading_source)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "heading_dest" -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text = resources.getString(R.string.mission_status_desc_heading_dest)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "awaiting_load" -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_loading_items)
                    missionStatusDescTv.text = resources.getString(R.string.mission_status_desc_loading_items)
                    missionStatusImg.setImageResource(R.drawable.package_loading)
                    confirmBtn.visibility = View.VISIBLE
                    confirmBtn.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            HttpClient.ddsApi.ackPackageLoad()?.execute()

                            fetchActiveMission()
                        }
                    }
                }
                "awaiting_unload" -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_unloading_items)
                    missionStatusDescTv.text = resources.getString(R.string.mission_status_desc_unloading_items)
                    missionStatusImg.setImageResource(R.drawable.package_unloading)
                    confirmBtn.visibility = View.VISIBLE
                    confirmBtn.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            HttpClient.ddsApi.ackPackageReceive()?.execute()

                            fetchActiveMission()
                        }
                    }
                }
                else -> {
                    missionStatusTv.text = resources.getString(R.string.mission_status_name_no_active_mission)
                    missionStatusDescTv.text = ""
                    missionStatusImg.setImageResource(R.drawable.ic_baseline_email_24)
                    confirmBtn.visibility = View.GONE
                }
            }
        }
    }
}