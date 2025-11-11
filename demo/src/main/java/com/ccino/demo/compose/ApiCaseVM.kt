package com.ccino.demo.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccino.demo.compose.data.GithubApiClient
import com.ccino.demo.compose.data.GithubUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed interface to represent the different UI states for the GitHub user request
sealed interface GithubUserUiState {
    object Idle : GithubUserUiState
    object Loading : GithubUserUiState
    data class Success(val user: GithubUser) : GithubUserUiState
    data class Error(val message: String) : GithubUserUiState
}


class ApiCaseVM : ViewModel() {
    // For the simple text field
    private val _nameFlow = MutableStateFlow("init")
    val nameFlow = _nameFlow.asStateFlow()

    fun updateName(newName: String) {
        _nameFlow.value = newName
    }

    // --- GitHub User Fetching Logic ---
    private val _uiState = MutableStateFlow<GithubUserUiState>(GithubUserUiState.Idle)
    val uiState: StateFlow<GithubUserUiState> = _uiState.asStateFlow()
    fun fetchGithubUser(username: String) {
        viewModelScope.launch {
            _uiState.value = GithubUserUiState.Loading
            delay(3000)
            try {
                val user = GithubApiClient.service.getUser(username)
                _uiState.value = GithubUserUiState.Success(user)
            } catch (e: Exception) {
                // In a real app, you'd want to handle different exceptions (network, 404, etc.)
                _uiState.value = GithubUserUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
