package com.example.nycopenjobs

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.nycopenjobs.ui.screens.HomeScreen
import com.example.nycopenjobs.ui.theme.NYCOpenJobsTheme
import androidx.compose.material3.Surface
import com.example.nycopenjobs.ui.screens.JobPostingsViewModel
import com.example.nycopenjobs.util.TAG

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "main activity oncreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NYCOpenJobsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val viewModel: JobPostingsViewModel by viewModels{JobPostingsViewModel.Factory}
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

