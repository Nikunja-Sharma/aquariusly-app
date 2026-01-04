package com.nikunja.aquariusly.di

import com.nikunja.aquariusly.data.remote.api.ModelApiService
import com.nikunja.aquariusly.data.remote.api.UserApiService
import com.nikunja.aquariusly.data.repository.ModelRepositoryImpl
import com.nikunja.aquariusly.domain.repository.ModelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Provides
    @Singleton
    fun provideModelApiService(retrofit: Retrofit): ModelApiService {
        return retrofit.create(ModelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        modelApiService: ModelApiService,
        userApiService: UserApiService
    ): ModelRepository {
        return ModelRepositoryImpl(modelApiService, userApiService)
    }
}
