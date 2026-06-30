package com.match.triple.games.sort3.di

import com.match.triple.games.sort3.ui.GameViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency-injection graph for the app.
 *
 * - [GameRepository] is a process-wide singleton built against the Android
 *   application [androidContext]; it owns the DataStore, Room DB and prefs.
 * - [GameViewModel] is resolved per ViewModelStore via the Koin `viewModel`
 *   DSL, receiving its repository through constructor injection.
 */
val appModule = module {
    viewModel { GameViewModel(get()) }
}
