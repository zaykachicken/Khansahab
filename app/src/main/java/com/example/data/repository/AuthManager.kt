package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.data.model.UserAccount
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthManager(private val context: Context) {

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val firebaseAuth: FirebaseAuth? = try {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            FirebaseAuth.getInstance()
        } else {
            null
        }
    } catch (e: Exception) {
        Log.w("AuthManager", "FirebaseAuth could not be initialized: ${e.message}")
        null
    }

    init {
        // Observe Firebase Auth state if available
        try {
            firebaseAuth?.addAuthStateListener { auth ->
                val fbUser = auth.currentUser
                if (fbUser != null) {
                    _currentUser.value = mapFirebaseUser(fbUser)
                } else if (_currentUser.value?.isAnonymous != true) {
                    // Do not overwrite demo/guest session unless explicitly logged out
                }
            }
            val initialUser = firebaseAuth?.currentUser
            if (initialUser != null) {
                _currentUser.value = mapFirebaseUser(initialUser)
            }
        } catch (e: Exception) {
            Log.w("AuthManager", "Failed to register auth state listener: ${e.message}")
        }
    }

    private fun mapFirebaseUser(user: FirebaseUser): UserAccount {
        return UserAccount(
            uid = user.uid,
            displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Foodie",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            authProvider = "Google"
        )
    }

    suspend fun signInWithGoogle(activity: Activity): Result<UserAccount> = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(activity)

        val serverClientId = try {
            activity.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            ""
        }

        val effectiveClientId = if (serverClientId.isNotBlank() && !serverClientId.startsWith("YOUR_")) {
            serverClientId
        } else {
            "demo-server-client-id.apps.googleusercontent.com"
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(effectiveClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val response = credentialManager.getCredential(
                context = activity,
                request = request
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Authenticate with Firebase if available
                if (firebaseAuth != null) {
                    try {
                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = firebaseAuth.signInWithCredential(authCredential).awaitTask()
                        val firebaseUser = authResult.user
                        if (firebaseUser != null) {
                            val account = mapFirebaseUser(firebaseUser)
                            _currentUser.value = account
                            return@withContext Result.success(account)
                        }
                    } catch (e: Exception) {
                        Log.w("AuthManager", "Firebase signInWithCredential warning: ${e.message}")
                    }
                }

                // Fallback to direct Google ID Token user
                val account = UserAccount(
                    uid = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName ?: "Google User",
                    email = googleIdTokenCredential.id,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    isAnonymous = false,
                    authProvider = "Google"
                )
                _currentUser.value = account
                return@withContext Result.success(account)
            } else {
                return@withContext Result.failure(IllegalStateException("Unsupported credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            return@withContext Result.failure(Exception("Google Sign-In was cancelled by user."))
        } catch (e: NoCredentialException) {
            return@withContext Result.failure(Exception("No Google accounts found on this device. You can use Quick Demo Sign-In."))
        } catch (e: GetCredentialException) {
            return@withContext Result.failure(Exception(e.localizedMessage ?: "Google Sign-In failed."))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    fun signInWithDemoGoogleAccount(
        name: String = "Yash Rabalam",
        email: String = "yashrabalam9@gmail.com"
    ): UserAccount {
        val account = UserAccount(
            uid = "google_yash_demo_101",
            displayName = name,
            email = email,
            photoUrl = null,
            isAnonymous = false,
            authProvider = "Google"
        )
        _currentUser.value = account
        return account
    }

    fun continueAsGuest(): UserAccount {
        val account = UserAccount(
            uid = "guest_${System.currentTimeMillis() % 10000}",
            displayName = "Guest Foodie",
            email = "guest@zayka.delivery",
            photoUrl = null,
            isAnonymous = true,
            authProvider = "Guest"
        )
        _currentUser.value = account
        return account
    }

    suspend fun signOut(activityContext: Context) = withContext(Dispatchers.IO) {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthManager", "Firebase signOut error", e)
        }
        try {
            val credentialManager = CredentialManager.create(activityContext)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("AuthManager", "CredentialManager clearCredentialState error", e)
        }
        _currentUser.value = null
    }
}

// Extension to await Task<T> without external dependency
suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
        addOnCanceledListener { cont.cancel(CancellationException("Task was cancelled")) }
    }
