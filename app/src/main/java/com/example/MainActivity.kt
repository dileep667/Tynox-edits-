package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TyoxBackground
import com.example.viewmodel.EditorViewModel
import com.example.viewmodel.HomeViewModel

sealed class AppScreen {
    object Home : AppScreen()
    data class Editor(val projectId: String) : AppScreen()
    object Templates : AppScreen()
    data class Export(val projectId: String) : AppScreen()
    object Settings : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TyoxBackground
                ) {
                    TyoxStudioApp()
                }
            }
        }
    }
}

@Composable
fun TyoxStudioApp() {
    val homeViewModel: HomeViewModel = viewModel()
    val editorViewModel: EditorViewModel = viewModel()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    // Handle system back navigation
    BackHandler(enabled = currentScreen !is AppScreen.Home) {
        currentScreen = when (val screen = currentScreen) {
            is AppScreen.Editor -> AppScreen.Home
            is AppScreen.Templates -> AppScreen.Home
            is AppScreen.Settings -> AppScreen.Home
            is AppScreen.Export -> AppScreen.Editor(screen.projectId)
            AppScreen.Home -> AppScreen.Home
        }
    }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                viewModel = homeViewModel,
                onOpenEditor = { projectId ->
                    currentScreen = AppScreen.Editor(projectId)
                },
                onOpenTemplates = {
                    currentScreen = AppScreen.Templates
                },
                onOpenSettings = {
                    currentScreen = AppScreen.Settings
                }
            )
        }

        is AppScreen.Editor -> {
            EditorScreen(
                projectId = screen.projectId,
                viewModel = editorViewModel,
                onNavigateBack = {
                    currentScreen = AppScreen.Home
                },
                onOpenExport = { projId ->
                    currentScreen = AppScreen.Export(projId)
                }
            )
        }

        is AppScreen.Templates -> {
            TemplatesScreen(
                homeViewModel = homeViewModel,
                onNavigateBack = {
                    currentScreen = AppScreen.Home
                },
                onOpenEditor = { projectId ->
                    currentScreen = AppScreen.Editor(projectId)
                }
            )
        }

        is AppScreen.Export -> {
            ExportScreen(
                projectId = screen.projectId,
                editorViewModel = editorViewModel,
                onNavigateBack = {
                    currentScreen = AppScreen.Editor(screen.projectId)
                }
            )
        }

        is AppScreen.Settings -> {
            SettingsScreen(
                onNavigateBack = {
                    currentScreen = AppScreen.Home
                }
            )
        }
    }
}

