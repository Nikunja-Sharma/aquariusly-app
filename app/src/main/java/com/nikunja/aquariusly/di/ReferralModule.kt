package com.nikunja.aquariusly.di

import android.content.SharedPreferences
import com.nikunja.aquariusly.data.remote.api.ReferralApiService
import com.nikunja.aquariusly.data.repository.ReferralRepositoryImpl
import com.nikunja.aquariusly.domain.repository.ReferralRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReferralModule {

    @Provides
    @Singleton
    fun provideReferralApiService(retrofit: Retrofit): ReferralApiService {
        return retrofit.create(ReferralApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReferralRepository(
        apiService: ReferralApiService,
        prefs: SharedPreferences
    ): ReferralRepository {
        return ReferralRepositoryImpl(apiService, prefs)
    }
}
