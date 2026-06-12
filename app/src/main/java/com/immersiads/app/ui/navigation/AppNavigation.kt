package com.immersiads.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.immersiads.app.ImmersiAdsApp
import com.immersiads.app.ui.feed.FeedScreen
import com.immersiads.app.ui.feed.FeedViewModel
import com.immersiads.app.ui.onboarding.OnboardingScreen
import com.immersiads.app.ui.onboarding.OnboardingViewModel
import com.immersiads.app.ui.player.PlayerScreen
import com.immersiads.app.ui.player.PlayerViewModel
import com.immersiads.app.ui.progress.ProgressScreen
import com.immersiads.app.ui.progress.ProgressViewModel
import com.immersiads.app.ui.settings.SettingsScreen
import com.immersiads.app.ui.settings.SettingsViewModel
import com.immersiads.app.ui.vocabulary.VocabularyScreen
import com.immersiads.app.ui.vocabulary.VocabularyViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Feed : Screen("feed")
    object Player : Screen("player/{adId}") {
        fun createRoute(adId: String) = "player/$adId"
    }
    object Vocabulary : Screen("vocabulary")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImmersiAdsApp
    val userPreferences = app.userPreferences

    val isOnboardingComplete by userPreferences.isOnboardingComplete.collectAsState(initial = false)

    val startDestination = if (isOnboardingComplete) Screen.Feed.route else Screen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            val viewModel = OnboardingViewModel(userPreferences)
            OnboardingScreen(
                viewModel = viewModel,
                onOnboardingComplete = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Feed.route) {
            val viewModel = FeedViewModel(app.adRepository, userPreferences)
            FeedScreen(
                viewModel = viewModel,
                onAdSelected = { adId ->
                    navController.navigate(Screen.Player.createRoute(adId))
                },
                onNavigateToVocabulary = { navController.navigate(Screen.Vocabulary.route) },
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("adId") { type = NavType.StringType })
        ) { backStackEntry ->
            val adId = backStackEntry.arguments?.getString("adId") ?: return@composable
            val viewModel = PlayerViewModel(app.adRepository, app.vocabularyRepository, userPreferences, adId)
            PlayerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Vocabulary.route) {
            val viewModel = VocabularyViewModel(app.vocabularyRepository, userPreferences)
            VocabularyScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Progress.route) {
            val viewModel = ProgressViewModel(userPreferences, app.vocabularyRepository)
            ProgressScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel = SettingsViewModel(userPreferences)
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
