package com.example.dogs.koin

import com.example.dogs.networking.instance.Instance
import com.example.dogs.utils.network.ConnectivityObserver
import com.example.dogs.utils.network.NetworkConnectivityObserver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Instance.api
    }

    single<DogsRepository> {
        DogsRepositoryImpl(get())
    }

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }
}