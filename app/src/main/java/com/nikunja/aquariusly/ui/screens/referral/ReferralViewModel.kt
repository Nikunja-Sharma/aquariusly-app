package com.nikunja.aquariusly.ui.screens.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikunja.aquariusly.domain.model.*
import com.nikunja.aquariusly.domain.repository.ApplyReferralResult
import com.nikunja.aquariusly.domain.repository.ReferralRepository
import com.nikunja.aquariusly.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReferralState(
    val isLoading: Boolean = false,
    val referralInfo: ReferralInfo? = null,
    val referralHistory: List<Referral> = emptyList(),
    val referralStats: ReferralStats? = null,
    val error: String? = null,
    val applyCodeDialogVisible: Boolean = false,
    val applyCodeInput: String = "",
    val applyCodeLoading: Boolean = false,
    val applyCodeResult: ApplyReferralResult? = null,
    val hasAppliedReferralCode: Boolean = false,
    val showRewardDialog: Boolean = false,
    val claimedReward: Reward? = null
)

sealed class ReferralEvent {
    data object LoadReferralInfo : ReferralEvent()
    data object LoadReferralHistory : ReferralEvent()
    data object LoadReferralStats : ReferralEvent()
    data object ShowApplyCodeDialog : ReferralEvent()
    data object HideApplyCodeDialog : ReferralEvent()
    data class UpdateApplyCodeInput(val code: String) : ReferralEvent()
    data object ApplyReferralCode : ReferralEvent()
    data class ClaimReward(val referralId: String) : ReferralEvent()
    data object DismissRewardDialog : ReferralEvent()
    data object ClearError : ReferralEvent()
    data object ClearApplyResult : ReferralEvent()
}

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val referralRepository: ReferralRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReferralState())
    val state: StateFlow<ReferralState> = _state.asStateFlow()

    init {
        _state.update { it.copy(hasAppliedReferralCode = referralRepository.hasAppliedReferralCode()) }
        loadAll()
    }

    fun onEvent(event: ReferralEvent) {
        when (event) {
            is ReferralEvent.LoadReferralInfo -> loadReferralInfo()
            is ReferralEvent.LoadReferralHistory -> loadReferralHistory()
            is ReferralEvent.LoadReferralStats -> loadReferralStats()
            is ReferralEvent.ShowApplyCodeDialog -> _state.update { it.copy(applyCodeDialogVisible = true) }
            is ReferralEvent.HideApplyCodeDialog -> _state.update { 
                it.copy(applyCodeDialogVisible = false, applyCodeInput = "", applyCodeResult = null) 
            }
            is ReferralEvent.UpdateApplyCodeInput -> _state.update { it.copy(applyCodeInput = event.code) }
            is ReferralEvent.ApplyReferralCode -> applyReferralCode()
            is ReferralEvent.ClaimReward -> claimReward(event.referralId)
            is ReferralEvent.DismissRewardDialog -> _state.update { it.copy(showRewardDialog = false, claimedReward = null) }
            is ReferralEvent.ClearError -> _state.update { it.copy(error = null) }
            is ReferralEvent.ClearApplyResult -> _state.update { it.copy(applyCodeResult = null) }
        }
    }

    private fun loadAll() {
        loadReferralInfo()
        loadReferralHistory()
        loadReferralStats()
    }

    private fun loadReferralInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = referralRepository.getReferralInfo()) {
                is Resource.Success -> {
                    _state.update { it.copy(referralInfo = result.data, isLoading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadReferralHistory() {
        viewModelScope.launch {
            when (val result = referralRepository.getReferralHistory()) {
                is Resource.Success -> {
                    _state.update { it.copy(referralHistory = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadReferralStats() {
        viewModelScope.launch {
            when (val result = referralRepository.getReferralStats()) {
                is Resource.Success -> {
                    _state.update { it.copy(referralStats = result.data) }
                }
                is Resource.Error -> {
                    // Stats are optional, don't show error
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun applyReferralCode() {
        val code = _state.value.applyCodeInput.trim().uppercase()
        if (code.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(applyCodeLoading = true) }
            when (val result = referralRepository.applyReferralCode(code)) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            applyCodeLoading = false,
                            applyCodeResult = result.data,
                            hasAppliedReferralCode = result.data?.success == true
                        ) 
                    }
                    if (result.data?.success == true) {
                        loadReferralInfo() // Refresh to show new rewards
                    }
                }
                is Resource.Error -> {
                    _state.update { 
                        it.copy(
                            applyCodeLoading = false,
                            applyCodeResult = ApplyReferralResult(
                                success = false,
                                message = result.message ?: "Failed to apply code",
                                reward = null,
                                referrerName = null
                            )
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun claimReward(referralId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = referralRepository.claimReward(referralId)) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            showRewardDialog = true,
                            claimedReward = result.data
                        ) 
                    }
                    loadReferralInfo() // Refresh rewards count
                    loadReferralHistory() // Refresh history
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
