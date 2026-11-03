package com.cyberverify.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.presentation.viewmodel.AuthViewModel
import com.cyberverify.presentation.viewmodel.ChatbotViewModel
import com.cyberverify.presentation.viewmodel.HistoryViewModel
import com.cyberverify.presentation.viewmodel.HomeViewModel
import com.cyberverify.presentation.viewmodel.ScanAnalysisState
import com.cyberverify.presentation.viewmodel.ScanViewModel
import com.cyberverify.ui.screen.ChatbotScreen
import com.cyberverify.ui.screen.DetailedReportScreen
import com.cyberverify.ui.screen.HistoryScreen
import com.cyberverify.ui.screen.HomeDashboardScreen
import com.cyberverify.ui.screen.LoginScreen
import com.cyberverify.ui.screen.OnboardingScreen
import com.cyberverify.ui.screen.ProfileScreen
import com.cyberverify.ui.screen.QuickSummaryScreen
import com.cyberverify.ui.screen.ScanProcessingScreen
import com.cyberverify.ui.screen.ScanScreen
import com.cyberverify.ui.screen.SplashScreen
import com.cyberverify.ui.theme.GovernmentBlue
import com.cyberverify.ui.theme.NavyBlue

sealed class RootScreen {
    object Splash : RootScreen()
    object Onboarding : RootScreen()
    object Login : RootScreen()
    object MainApp : RootScreen()
    data class Processing(val fileName: String, val mediaType: MediaType, val report: ScanReport?) : RootScreen()
    data class QuickSummary(val report: ScanReport) : RootScreen()
    data class DetailedReport(val report: ScanReport) : RootScreen()
    data class Results(val report: ScanReport) : RootScreen()
}

enum class BottomTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    SCAN("Scan", Icons.Default.Security),
    ASSISTANT("Assistant", Icons.Default.SmartToy),
    HISTORY("History", Icons.Default.History),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun MainAppNavigation(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    scanViewModel: ScanViewModel,
    historyViewModel: HistoryViewModel,
    chatbotViewModel: ChatbotViewModel,
    modifier: Modifier = Modifier
) {
    var currentRootScreen by remember { mutableStateOf<RootScreen>(RootScreen.Splash) }
    var currentBottomTab by remember { mutableStateOf(BottomTab.HOME) }

    val scanAnalysisState by scanViewModel.analysisState.collectAsStateWithLifecycle()
    val selectedFileName by scanViewModel.selectedFileName.collectAsStateWithLifecycle()
    val activeTab by scanViewModel.activeTab.collectAsStateWithLifecycle()
    val audioPlayerState by scanViewModel.audioPlayerState.collectAsStateWithLifecycle()

    // Monitor ScanViewModel analysis state to trigger processing / summary screens automatically
    LaunchedEffect(scanAnalysisState) {
        val state = scanAnalysisState
        if (state is ScanAnalysisState.Analyzing) {
            val name = selectedFileName ?: "media_file"
            currentRootScreen = RootScreen.Processing(name, activeTab, null)
        } else if (state is ScanAnalysisState.Success) {
            val name = selectedFileName ?: "media_file"
            currentRootScreen = RootScreen.Processing(name, activeTab, state.report)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentRootScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "rootContentTransition"
        ) { rootScreen ->
            when (rootScreen) {
                is RootScreen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentRootScreen = RootScreen.Onboarding
                        }
                    )
                }

                is RootScreen.Onboarding -> {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            currentRootScreen = RootScreen.Login
                        }
                    )
                }

                is RootScreen.Login -> {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            currentRootScreen = RootScreen.MainApp
                        }
                    )
                }

                is RootScreen.MainApp -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = NavyBlue,
                                contentColor = Color.White
                            ) {
                                BottomTab.entries.forEach { tab ->
                                    val isSelected = currentBottomTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentBottomTab = tab },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = Color.White,
                                            indicatorColor = GovernmentBlue,
                                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentBottomTab) {
                                BottomTab.HOME -> {
                                    HomeDashboardScreen(
                                        homeViewModel = homeViewModel,
                                        authViewModel = authViewModel,
                                        onNavigateToScan = { mediaType ->
                                            scanViewModel.setActiveTab(mediaType)
                                            currentBottomTab = BottomTab.SCAN
                                        },
                                        onNavigateToReport = { report ->
                                            currentRootScreen = RootScreen.QuickSummary(report)
                                        },
                                        onNavigateToHistory = {
                                            currentBottomTab = BottomTab.HISTORY
                                        },
                                        onNavigateToAssistant = {
                                            currentBottomTab = BottomTab.ASSISTANT
                                        }
                                    )
                                }

                                BottomTab.SCAN -> {
                                    ScanScreen(viewModel = scanViewModel)
                                }

                                BottomTab.ASSISTANT -> {
                                    ChatbotScreen(viewModel = chatbotViewModel)
                                }

                                BottomTab.HISTORY -> {
                                    HistoryScreen(
                                        historyViewModel = historyViewModel,
                                        onSelectReport = { report ->
                                            currentRootScreen = RootScreen.QuickSummary(report)
                                        }
                                    )
                                }

                                BottomTab.PROFILE -> {
                                    ProfileScreen(
                                        authViewModel = authViewModel,
                                        historyViewModel = historyViewModel,
                                        onLogout = {
                                            currentRootScreen = RootScreen.Login
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is RootScreen.Processing -> {
                    ScanProcessingScreen(
                        fileName = rootScreen.fileName,
                        mediaType = rootScreen.mediaType,
                        report = rootScreen.report,
                        onProcessingFinished = { completedReport ->
                            currentRootScreen = RootScreen.QuickSummary(completedReport)
                        }
                    )
                }

                is RootScreen.QuickSummary -> {
                    QuickSummaryScreen(
                        report = rootScreen.report,
                        onViewDetailedReport = {
                            currentRootScreen = RootScreen.DetailedReport(rootScreen.report)
                        },
                        onCheckAnotherFile = {
                            scanViewModel.resetAnalysisState()
                            scanViewModel.clearSelectedMedia()
                            currentRootScreen = RootScreen.MainApp
                            currentBottomTab = BottomTab.SCAN
                        }
                    )
                }

                is RootScreen.DetailedReport -> {
                    DetailedReportScreen(
                        report = rootScreen.report,
                        onBackToSummary = {
                            currentRootScreen = RootScreen.QuickSummary(rootScreen.report)
                        },
                        playerState = audioPlayerState,
                        onPlayPauseToggle = {
                            scanViewModel.audioPlaybackManager.togglePlayPause()
                        },
                        onSeekTo = { targetMs ->
                            scanViewModel.audioPlaybackManager.seekTo(targetMs)
                        }
                    )
                }

                is RootScreen.Results -> {
                    QuickSummaryScreen(
                        report = rootScreen.report,
                        onViewDetailedReport = {
                            currentRootScreen = RootScreen.DetailedReport(rootScreen.report)
                        },
                        onCheckAnotherFile = {
                            scanViewModel.resetAnalysisState()
                            scanViewModel.clearSelectedMedia()
                            currentRootScreen = RootScreen.MainApp
                            currentBottomTab = BottomTab.SCAN
                        }
                    )
                }
            }
        }
    }
}
