package com.example.dds

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                Firebase.auth.signOut()

                navController.navigate(R.id.action_trackMissionFragment_to_loginFragment)
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HttpClient.init()

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(findViewById(R.id.my_toolbar))
//        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(R.id.loginFragment, R.id.trackMissionFragment))
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