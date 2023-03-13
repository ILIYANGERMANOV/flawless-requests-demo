package com.flawlessrequests.di

import com.flawlessrequests.network.ktorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModuleDI {
    @Singleton
    @Provides
    fun provideKtorClient(): HttpClient = ktorClient()
}