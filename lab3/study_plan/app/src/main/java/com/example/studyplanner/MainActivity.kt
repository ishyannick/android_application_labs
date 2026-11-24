package com.example.studyplanner

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.studyplanner.ui.navigation.StudyPlannerNavGraph
import com.example.studyplanner.ui.theme.StudyPlannerTheme
import com.example.studyplanner.util.SyncNotifier
import com.example.studyplanner.viewmodel.StudyTaskViewModel

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val viewModel: StudyTaskViewModel by viewModels {
        val app = application as StudyPlannerApplication
        StudyTaskViewModel.Factory(app, app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SyncNotifier.createChannel(this)
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            StudyPlannerTheme {
                StudyPlannerNavGraph(viewModel)
            }
        }
    }
}
