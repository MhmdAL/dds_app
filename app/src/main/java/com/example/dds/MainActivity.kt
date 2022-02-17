package com.example.dds

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HttpClient.init()

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        val navController = navHostFragment.navController

//        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(R.id.loginFragment, R.id.createMissionFragment))
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//
//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setupWithNavController(navController)

        bottomNav.visibility = View.GONE

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.create_mission_menu_item -> navController.navigate(R.id.createMissionFragment)
                R.id.track_mission_menu_item -> navController.navigate(R.id.trackMissionFragment)
            }

            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.createMissionFragment || destination.id == R.id.trackMissionFragment) {
                bottomNav.visibility = View.GONE
            } else {
                bottomNav.visibility = View.GONE
            }
        }
    }
}