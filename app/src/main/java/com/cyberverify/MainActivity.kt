package com.cyberverify

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyberverify.data.local.AppDatabase
import com.cyberverify.data.repository.ScanRepository
import com.cyberverify.presentation.viewmodel.AuthViewModel
import com.cyberverify.presentation.viewmodel.HistoryViewModel
import com.cyberverify.presentation.viewmodel.HomeViewModel
import com.cyberverify.presentation.viewmodel.ScanViewModel
import com.cyberverify.ui.navigation.MainAppNavigation
import com.cyberverify.ui.theme.CyberVerifyTheme

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: ScanRepository

    private val authViewModel by viewModels<AuthViewModel>()

    private val homeViewModel by viewModels<HomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
        }
    }

    private val scanViewModel by viewModels<ScanViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScanViewModel(repository) as T
            }
        }
    }

    private val historyViewModel by viewModels<HistoryViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getInstance(applicationContext)
        repository = ScanRepository(database.scanDao())

        requestPermissions()

        setContent {
            CyberVerifyTheme {
                MainAppNavigation(
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    scanViewModel = scanViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            requestPermissions(permissions, 1001)
        }
    }
}
