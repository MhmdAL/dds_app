package com.example.ecarrier

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackMissionFragment : Fragment(R.layout.fragment_track_mission) {

    private val missionStatusUpdateInterval = 2000L
    private var handler: Handler? = null

    var missionStatusUpdater: Runnable = object : Runnable {
        override fun run() {
            try {
                fetchActiveMission()
            } finally {
                handler?.postDelayed(this, missionStatusUpdateInterval)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireActivity().findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

        handler = Handler()
        missionStatusUpdater.run()
    }

    override fun onDestroy() {
        super.onDestroy()

        handler?.removeCallbacks(missionStatusUpdater)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overflow, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                Firebase.auth.signOut()

                findNavController().navigate(R.id.action_trackMissionFragment_to_loginFragment)
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchActiveMission()

        val fab = view.findViewById<FloatingActionButton>(R.id.start_mission_fab)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_trackMissionFragment_to_createMissionFragment)
        }

        val swipeLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_container);
        swipeLayout.setOnRefreshListener {
            fetchActiveMission()
            swipeLayout.isRefreshing = false;
        }
    }

    fun fetchActiveMission() {
        lifecycleScope.launch(Dispatchers.IO) {
            val mission =
                HttpClient.eCarrierApi.getActiveMission("Bearer " + UserState.userToken)?.execute()
                    ?.body()

            val userType = when (UserState.userId) {
                mission?.sender_id -> UserType.Sender
                mission?.recepient_id -> UserType.Recipient
                else -> UserType.Unknown
            }

            withContext(Dispatchers.Main) {
                updateUiForMission(mission, userType)
            }
        }
    }

    fun updateUiForMission(mission: Mission?, userType: UserType) {
        view?.let {
            val missionStatusTv = it.findViewById<TextView>(R.id.mission_status_tv)
            val missionStatusDescTv = it.findViewById<TextView>(R.id.mission_status_desc_tv)
            val missionStatusImg = it.findViewById<ImageView>(R.id.mission_status_img)
            val confirmBtn = it.findViewById<Button>(R.id.confirm_btn)
            val startMissionFab = it.findViewById<FloatingActionButton>(R.id.start_mission_fab)

            startMissionFab.visibility = if (mission?.id == null) View.VISIBLE else View.GONE

            when (mission?.mission_status) {
                "new_mission" -> {
                    missionStatusTv.text =
                        resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text =
                        resources.getString(R.string.mission_status_desc_heading_source)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "heading_source" -> {
                    missionStatusTv.text =
                        resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text =
                        resources.getString(R.string.mission_status_desc_heading_source)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "awaiting_load" -> {
                    if(userType == UserType.Sender) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_loading_items_s)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_loading_items_s)
                        missionStatusImg.setImageResource(R.drawable.package_loading)
                        confirmBtn.visibility = View.VISIBLE
                        confirmBtn.setOnClickListener {
                            lifecycleScope.launch(Dispatchers.IO) {
                                HttpClient.eCarrierApi.ackPackageLoad()?.execute()

                                fetchActiveMission()
                            }
                        }
                    }else if(userType == UserType.Recipient) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_loading_items_r)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_loading_items_r)
                        missionStatusImg.setImageResource(R.drawable.package_loading)
                        confirmBtn.visibility = View.GONE
                    }
                }
                "heading_dest" -> {
                    missionStatusTv.text =
                        resources.getString(R.string.mission_status_name_in_progress)
                    missionStatusDescTv.text =
                        resources.getString(R.string.mission_status_desc_heading_dest)
                    missionStatusImg.setImageResource(R.drawable.drone)
                    confirmBtn.visibility = View.GONE
                }
                "awaiting_recipient" -> {
                    if(userType == UserType.Sender) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_awaiting_recipient_s)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_awaiting_recipient_s)
                        missionStatusImg.setImageResource(R.drawable.drone)
                        confirmBtn.visibility = View.GONE
                    }else if(userType == UserType.Recipient) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_awaiting_recipient_r)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_awaiting_recipient_r)
                        missionStatusImg.setImageResource(R.drawable.drone)
                        confirmBtn.visibility = View.GONE
                    }
                }
                "awaiting_unload" -> {
                    if(userType == UserType.Sender) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_unloading_items_s)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_unloading_items_s)
                        missionStatusImg.setImageResource(R.drawable.package_unloading)
                        confirmBtn.visibility = View.GONE
                    }else if(userType == UserType.Recipient) {
                        missionStatusTv.text =
                            resources.getString(R.string.mission_status_name_unloading_items_r)
                        missionStatusDescTv.text =
                            resources.getString(R.string.mission_status_desc_unloading_items_r)
                        missionStatusImg.setImageResource(R.drawable.package_unloading)
                        confirmBtn.visibility = View.VISIBLE
                        confirmBtn.setOnClickListener {
                            lifecycleScope.launch(Dispatchers.IO) {
                                HttpClient.eCarrierApi.ackPackageReceive()?.execute()

                                fetchActiveMission()
                            }
                        }
                    }
                }
                else -> {
                    missionStatusTv.text =
                        resources.getString(R.string.mission_status_name_no_active_mission)
                    missionStatusDescTv.text = ""
                    missionStatusImg.setImageResource(R.drawable.ic_baseline_email_24)
                    confirmBtn.visibility = View.GONE
                }
            }
        }
    }
}