package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val badgeNumber: String = "ID-9482",
    val role: String = "Verified Member",
    val isLoggedIn: Boolean = true
)

class AuthViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun login(name: String, email: String, badgeNumber: String = "ID-9482") {
        _userProfile.value = UserProfile(
            name = name.ifBlank { "Alex Morgan" },
            email = email.ifBlank { "alex.morgan@example.com" },
            badgeNumber = badgeNumber.ifBlank { "ID-9482" },
            role = "Verified Member",
            isLoggedIn = true
        )
    }

    fun logout() {
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
    }
}
