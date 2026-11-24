package com.example.studyplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyplanner.ui.screens.DetailScreen
import com.example.studyplanner.ui.screens.EditScreen
import com.example.studyplanner.ui.screens.ListScreen
import com.example.studyplanner.viewmodel.StudyTaskViewModel

private const val LIST = "list"
private const val EDIT = "edit/{taskId}"
private const val DETAIL = "detail/{taskId}"

@Composable
fun StudyPlannerNavGraph(viewModel: StudyTaskViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LIST) {
        composable(LIST) {
            ListScreen(
                viewModel = viewModel,
                onAdd = { navController.navigate("edit/new") },
                onEdit = { id -> navController.navigate("edit/$id") },
                onOpen = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = EDIT,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            EditScreen(
                taskId = entry.arguments?.getString("taskId") ?: "new",
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("taskId") ?: return@composable
            DetailScreen(
                taskId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("edit/$id") }
            )
        }
    }
}
