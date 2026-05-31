package com.match.triple.games.sort3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.match.triple.games.sort3.ui.screens.GameplayScreen
import com.match.triple.games.sort3.ui.screens.MenuScreen
import com.match.triple.games.sort3.ui.screens.RulesScreen
import com.match.triple.games.sort3.ui.screens.ShopScreen
import com.match.triple.games.sort3.ui.screens.TrophyScreen

/** Type-safe-ish route table for the single-activity navigation graph. */
object Routes {
    const val MENU = "menu"
    const val GAME = "game"
    const val SHOP = "shop"
    const val TROPHIES = "trophies"
    const val RULES = "rules"
}

/**
 * Root of the app. Hosts one shared [GameViewModel] (activity-scoped) so every screen reads
 * and writes the same persisted state, and drives navigation between the five screens.
 */
@Composable
fun FishHunterApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.MENU,
        modifier = modifier
    ) {
        composable(Routes.MENU) {
            MenuScreen(
                uiState = uiState,
                onPlay = { navController.navigate(Routes.GAME) },
                onShop = { navController.navigate(Routes.SHOP) },
                onTrophies = { navController.navigate(Routes.TROPHIES) },
                onRules = { navController.navigate(Routes.RULES) }
            )
        }
        composable(Routes.GAME) {
            GameplayScreen(
                uiState = uiState,
                onRunFinished = viewModel::commitRun,
                onExit = { navController.popBackStack() }
            )
        }
        composable(Routes.SHOP) {
            ShopScreen(
                uiState = uiState,
                onBuy = viewModel::buyBooster,
                onConsumeMessage = viewModel::consumeMessage,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.TROPHIES) {
            TrophyScreen(uiState = uiState, onBack = { navController.popBackStack() })
        }
        composable(Routes.RULES) {
            RulesScreen(onBack = { navController.popBackStack() })
        }
    }
}
