package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val badgeNumber: String = "ID-9482",
    val role: String = "Verified Member",
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _userProfile.value = UserProfile(
                name = currentUser.displayName?.ifBlank { null } ?: currentUser.email?.substringBefore("@") ?: "Alex Morgan",
                email = currentUser.email ?: "alex.morgan@example.com",
                badgeNumber = "ID-${Math.abs(currentUser.uid.hashCode()) % 9000 + 1000}",
                role = "Verified Member",
                isLoggedIn = true
            )
        } else {
            _userProfile.value = UserProfile(isLoggedIn = false)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        if (email.isBlank() || password.isBlank()) {
            _userProfile.value = _userProfile.value.copy(errorMessage = "Email and password cannot be blank")
            onResult(false, "Email and password cannot be blank")
            return
        }

        _userProfile.value = _userProfile.value.copy(isLoading = true, errorMessage = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                val name = user?.displayName ?: email.substringBefore("@")
                _userProfile.value = UserProfile(
                    name = name.ifBlank { "Alex Morgan" },
                    email = user?.email ?: email,
                    badgeNumber = user?.uid?.let { "ID-${Math.abs(it.hashCode()) % 9000 + 1000}" } ?: "ID-9482",
                    role = "Verified Member",
                    isLoggedIn = true,
                    isLoading = false
                )
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                _userProfile.value = _userProfile.value.copy(
                    isLoading = false,
                    errorMessage = exception.localizedMessage ?: "Authentication failed"
                )
                onResult(false, exception.localizedMessage)
            }
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        if (email.isBlank() || password.isBlank()) {
            _userProfile.value = _userProfile.value.copy(errorMessage = "Email and password cannot be blank")
            onResult(false, "Email and password cannot be blank")
            return
        }

        _userProfile.value = _userProfile.value.copy(isLoading = true, errorMessage = null)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.ifBlank { email.substringBefore("@") })
                    .build()
                user?.updateProfile(profileUpdates)

                val displayName = name.ifBlank { user?.displayName ?: email.substringBefore("@") }
                _userProfile.value = UserProfile(
                    name = displayName,
                    email = user?.email ?: email,
                    badgeNumber = user?.uid?.let { "ID-${Math.abs(it.hashCode()) % 9000 + 1000}" } ?: "ID-9482",
                    role = "Verified Member",
                    isLoggedIn = true,
                    isLoading = false
                )
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                _userProfile.value = _userProfile.value.copy(
                    isLoading = false,
                    errorMessage = exception.localizedMessage ?: "Registration failed"
                )
                onResult(false, exception.localizedMessage)
            }
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = UserProfile(isLoggedIn = false)
    }

    fun clearError() {
        _userProfile.value = _userProfile.value.copy(errorMessage = null)
    }
}
