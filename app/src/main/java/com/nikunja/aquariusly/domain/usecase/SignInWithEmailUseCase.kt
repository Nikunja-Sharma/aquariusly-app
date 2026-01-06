package com.nikunja.aquariusly.domain.usecase

import com.nikunja.aquariusly.domain.model.User
import com.nikunja.aquariusly.domain.repository.AuthRepository
import com.nikunja.aquariusly.domain.util.Resource
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(firebaseIdToken: String): Resource<User> {
        return authRepository.signInWithEmail(firebaseIdToken)
    }
}
