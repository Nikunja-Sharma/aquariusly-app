package com.nikunja.aquariusly.di

import com.google.gson.Gson
import com.nikunja.aquariusly.data.remote.api.ChatApiService
import com.nikunja.aquariusly.data.repository.ChatRepositoryImpl
import com.nikunja.aquariusly.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        chatApiService: ChatApiService,
        gson: Gson
    ): ChatRepository {
        return ChatRepositoryImpl(chatApiService, gson)
    }
}
