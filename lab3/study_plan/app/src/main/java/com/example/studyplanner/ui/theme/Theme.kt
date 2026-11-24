package com.example.studyplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PlannerColors = lightColorScheme(
    primary = Color(0xFF345995),
    secondary = Color(0xFFF0A202),
    tertiary = Color(0xFF2A9D8F),
    background = Color(0xFFF7F8FC),
    surface = Color.White
)

@Composable
fun StudyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlannerColors, content = content)
}
