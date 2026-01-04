package com.nikunja.aquariusly.domain.usecase

import com.nikunja.aquariusly.domain.model.User
import com.nikunja.aquariusly.domain.repository.AuthRepository
import com.nikunja.aquariusly.domain.util.Resource
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Resource<User> {
        return authRepository.getCurrentUser(forceRefresh)
    }
}
