package com.example.studdyplan.ui.navigation

sealed class Screen(val route: String) {
    object List : Screen("list_screen")
    object AddEdit : Screen("add_edit_screen?taskId={taskId}") {
        fun passId(taskId: String? = null): String {
            return if (taskId != null) "add_edit_screen?taskId=$taskId" else "add_edit_screen"
        }
    }
}