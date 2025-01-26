package com.cristiancizmar.exchangerates.ui

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cristiancizmar.exchangerates.R
import com.cristiancizmar.exchangerates.databinding.ActivityMainBinding
import com.cristiancizmar.exchangerates.util.isInternetAvailable
import com.cristiancizmar.exchangerates.util.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            setInternetAvailable(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            setInternetAvailable(false)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            setInternetAvailable(false)
        }
    }

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigation()
        setInternetAvailable(isInternetAvailable(this))
    }

    override fun onResume() {
        super.onResume()
        listenNetworkState()
    }

    override fun onPause() {
        super.onPause()
        stopListenNetworkState()
    }

    fun setProgressBarLoading(loading: Boolean) {
        binding.loadingPb.isVisible = loading
    }

    private fun initBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.activityNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)
    }

    private fun setInternetAvailable(available: Boolean) {
        runOnUiThread {
            binding.noInternetTv.isVisible = !available
        }
    }

    private fun listenNetworkState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val connectivityManager: ConnectivityManager = getSystemService()!!
            listenNetworkState(connectivityManager)
        }
    }

    private fun stopListenNetworkState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val connectivityManager: ConnectivityManager = getSystemService()!!
            stopListeningNetworkState(connectivityManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun listenNetworkState(connectivityManager: ConnectivityManager) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun stopListeningNetworkState(connectivityManager: ConnectivityManager) {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}