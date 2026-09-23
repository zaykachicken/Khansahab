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

    private val _currentUser = MutableStateFlow<UserAccount?>(
        UserAccount(
            uid = "customer_foodie_101",
            displayName = "Zayka Foodie",
            email = "foodie@zayka.delivery",
            photoUrl = null,
            isAnonymous = false,
            authProvider = "Direct",
            role = "CUSTOMER"
        )
    )
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    fun updateProfile(name: String, phoneOrEmail: String) {
        val current = _currentUser.value ?: UserAccount(
            uid = "customer_foodie_101",
            displayName = name,
            email = phoneOrEmail,
            role = "CUSTOMER"
        )
        _currentUser.value = current.copy(
            displayName = name.ifBlank { current.displayName },
            email = phoneOrEmail.ifBlank { current.email }
        )
    }

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
                    // Do not overwrite active guest session unless explicitly logged out
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
        val userEmail = user.email ?: ""
        return UserAccount(
            uid = user.uid,
            displayName = user.displayName ?: userEmail.substringBefore("@").ifBlank { "Foodie" },
            email = userEmail,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            authProvider = "Google",
            role = "CUSTOMER"
        )
    }

    suspend fun signInWithGoogle(activity: Activity): Result<UserAccount> = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(activity)

        val serverClientId = try {
            val resId = activity.resources.getIdentifier("default_web_client_id", "string", activity.packageName)
            val dynamicValue = if (resId != 0) activity.getString(resId).trim() else ""
            if (dynamicValue.isNotBlank() && !dynamicValue.startsWith("YOUR_")) {
                dynamicValue
            } else {
                activity.getString(R.string.default_web_client_id).trim()
            }
        } catch (e: Exception) {
            try {
                activity.getString(R.string.default_web_client_id).trim()
            } catch (e2: Exception) {
                ""
            }
        }

        if (serverClientId.isBlank() || serverClientId.startsWith("YOUR_")) {
            return@withContext Result.failure(
                IllegalStateException(
                    "Google Web Client ID is not configured. Please ensure google-services.json containing the OAuth Web Client ID (client_type 3) is placed in the app directory."
                )
            )
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
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
                val userEmail = googleIdTokenCredential.id
                val account = UserAccount(
                    uid = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName ?: "Google User",
                    email = userEmail,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    isAnonymous = false,
                    authProvider = "Google",
                    role = "CUSTOMER"
                )
                _currentUser.value = account
                return@withContext Result.success(account)
            } else {
                return@withContext Result.failure(IllegalStateException("Unsupported credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            return@withContext Result.failure(Exception("Google Sign-In was cancelled."))
        } catch (e: NoCredentialException) {
            return@withContext Result.failure(Exception("No Google account selected or found on this device."))
        } catch (e: GetCredentialException) {
            return@withContext Result.failure(Exception(e.localizedMessage ?: "Google Sign-In failed. Please try again."))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        if (firebaseAuth == null) {
            return@withContext Result.failure(Exception("Firebase Auth is not available."))
        }
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).awaitTask()
            val user = authResult.user
            if (user != null) {
                val account = mapFirebaseUser(user)
                _currentUser.value = account
                return@withContext Result.success(account)
            } else {
                return@withContext Result.failure(Exception("Signup failed."))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        if (firebaseAuth == null) {
            return@withContext Result.failure(Exception("Firebase Auth is not available."))
        }
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).awaitTask()
            val user = authResult.user
            if (user != null) {
                val account = mapFirebaseUser(user)
                _currentUser.value = account
                return@withContext Result.success(account)
            } else {
                return@withContext Result.failure(Exception("Sign in failed."))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    fun setUserAccount(account: UserAccount) {
        _currentUser.value = account
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

    suspend fun signInWithPhoneNumber(phoneNumber: String, otpCode: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        try {
            val cleanedNumber = phoneNumber.trim()
            if (cleanedNumber.length < 10) {
                return@withContext Result.failure(Exception("Please enter a valid 10-digit mobile number."))
            }
            if (otpCode.length != 6) {
                return@withContext Result.failure(Exception("Please enter a valid 6-digit OTP code."))
            }
            val account = UserAccount(
                uid = "phone_${cleanedNumber.replace("+", "").replace(" ", "")}",
                displayName = "Foodie ($cleanedNumber)",
                email = "${cleanedNumber.replace("+", "").replace(" ", "")}@zayka.phone",
                photoUrl = null,
                isAnonymous = false,
                authProvider = "Phone",
                role = "CUSTOMER"
            )
            _currentUser.value = account
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
