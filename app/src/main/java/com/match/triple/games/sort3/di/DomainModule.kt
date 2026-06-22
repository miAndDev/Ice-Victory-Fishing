package com.match.triple.games.sort3.di

import com.match.triple.games.sort3.ActivityViewModel
import com.match.triple.games.sort3.core.base.SignalCore
import com.match.triple.games.sort3.core.dataStore.DataStoreRepo
import com.match.triple.games.sort3.core.dataStore.DataStoreRepoImpl
import com.match.triple.games.sort3.core.gaid.GaidRepo
import com.match.triple.games.sort3.core.referer.RefRepo
import com.match.triple.games.sort3.domain.CalpasionDomain
import com.match.triple.games.sort3.domain.SavedDataDomain
import com.match.triple.games.sort3.domain.creation.CreationDomain
import com.match.triple.games.sort3.domain.datastore.SavedDomain
import com.match.triple.games.sort3.reducer.AppReducer
import com.match.triple.games.sort3.reducer.AppReducerPosition
import com.match.triple.games.sort3.reducer.AppReducerSetter
import com.match.triple.games.sort3.reducer.AppReducerState
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val domainModule = module {
    singleOf(::SavedDataDomain)
    singleOf(::RefRepo)
    singleOf(::GaidRepo)
    singleOf(::AppReducer) binds arrayOf(
        AppReducerState::class,
        AppReducerPosition::class,
        AppReducerSetter::class,
    )
    factoryOf(::CalpasionDomain)
    singleOf(::DataStoreRepoImpl) bind DataStoreRepo::class
    singleOf(::SavedDomain)
    singleOf(::SignalCore)
    singleOf(::CreationDomain)
    viewModelOf(::ActivityViewModel)
}
