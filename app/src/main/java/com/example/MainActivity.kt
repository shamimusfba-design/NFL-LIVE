package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NflRoyalBlue

enum class AppScreen {
  HOME,
  PRODUCTION_ENTRY,
  QUALITY_ENTRY,
  NPT_SUPERVISOR,
  SUPERVISOR_SETUP,
  TV_DASHBOARD,
  HISTORY_SYNC,
  SETTINGS
}

class MainActivity : ComponentActivity() {

  private lateinit var repository: NflRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    repository = NflRepository(applicationContext)

    setContent {
      MyApplicationTheme {
        var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
        val currentRole by repository.currentRole.collectAsState()

        // Handle back press to always return cleanly to HOME
        if (currentScreen != AppScreen.HOME) {
          BackHandler {
            currentScreen = AppScreen.HOME
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            // Do not show bottom nav on TV dashboard so it displays truly fullscreen 16:9
            if (currentScreen != AppScreen.TV_DASHBOARD) {
              NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
              ) {
                NavigationBarItem(
                  selected = currentScreen == AppScreen.HOME,
                  onClick = { currentScreen = AppScreen.HOME },
                  icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                  label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NflRoyalBlue,
                    selectedTextColor = NflRoyalBlue,
                    indicatorColor = Color(0xFFE3F2FD)
                  ),
                  modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                  selected = currentScreen == AppScreen.PRODUCTION_ENTRY || currentScreen == AppScreen.QUALITY_ENTRY,
                  onClick = {
                    currentScreen = when (currentRole) {
                      UserRole.QUALITY -> AppScreen.QUALITY_ENTRY
                      else -> AppScreen.PRODUCTION_ENTRY
                    }
                  },
                  icon = { Icon(Icons.Default.EditNote, contentDescription = "Entry") },
                  label = { Text("Entry", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NflRoyalBlue,
                    selectedTextColor = NflRoyalBlue,
                    indicatorColor = Color(0xFFE3F2FD)
                  ),
                  modifier = Modifier.testTag("nav_entry")
                )

                NavigationBarItem(
                  selected = currentScreen == AppScreen.TV_DASHBOARD,
                  onClick = { currentScreen = AppScreen.TV_DASHBOARD },
                  icon = { Icon(Icons.Default.Tv, contentDescription = "TV Dashboard") },
                  label = { Text("TV Live", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NflRoyalBlue,
                    selectedTextColor = NflRoyalBlue,
                    indicatorColor = Color(0xFFE3F2FD)
                  ),
                  modifier = Modifier.testTag("nav_tv")
                )

                NavigationBarItem(
                  selected = currentScreen == AppScreen.HISTORY_SYNC,
                  onClick = { currentScreen = AppScreen.HISTORY_SYNC },
                  icon = { Icon(Icons.Default.History, contentDescription = "History") },
                  label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NflRoyalBlue,
                    selectedTextColor = NflRoyalBlue,
                    indicatorColor = Color(0xFFE3F2FD)
                  ),
                  modifier = Modifier.testTag("nav_history")
                )
              }
            }
          }
        ) { innerPadding ->
          Surface(
            modifier = Modifier
              .fillMaxSize()
              .padding(if (currentScreen == AppScreen.TV_DASHBOARD) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
          ) {
            when (currentScreen) {
              AppScreen.HOME -> HomeScreen(
                repository = repository,
                onNavigateToProduction = { currentScreen = AppScreen.PRODUCTION_ENTRY },
                onNavigateToQuality = { currentScreen = AppScreen.QUALITY_ENTRY },
                onNavigateToNpt = { currentScreen = AppScreen.NPT_SUPERVISOR },
                onNavigateToSetup = { currentScreen = AppScreen.SUPERVISOR_SETUP },
                onNavigateToTv = { currentScreen = AppScreen.TV_DASHBOARD },
                onNavigateToHistory = { currentScreen = AppScreen.HISTORY_SYNC },
                onNavigateToSettings = { currentScreen = AppScreen.SETTINGS }
              )
              AppScreen.PRODUCTION_ENTRY -> ProductionEntryScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME },
                onViewHistory = { currentScreen = AppScreen.HISTORY_SYNC }
              )
              AppScreen.QUALITY_ENTRY -> QualityEntryScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME },
                onViewHistory = { currentScreen = AppScreen.HISTORY_SYNC }
              )
              AppScreen.NPT_SUPERVISOR -> NptSupervisorScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME }
              )
              AppScreen.SUPERVISOR_SETUP -> SupervisorSetupScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME }
              )
              AppScreen.TV_DASHBOARD -> TvDashboardScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME }
              )
              AppScreen.HISTORY_SYNC -> HistorySyncScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME }
              )
              AppScreen.SETTINGS -> SettingsScreen(
                repository = repository,
                onNavigateBack = { currentScreen = AppScreen.HOME }
              )
            }
          }
        }
      }
    }
  }
}
