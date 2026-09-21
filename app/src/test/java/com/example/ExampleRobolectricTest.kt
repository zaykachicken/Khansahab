package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Zayka", appName)
    val webClientId = context.getString(R.string.default_web_client_id)
    org.junit.Assert.assertTrue(webClientId.isNotEmpty())
  }

  @Test
  fun `test demo and guest auth accounts`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authManager = com.example.data.repository.AuthManager(context)

    val demoUser = authManager.signInWithDemoGoogleAccount("Yash Rabalam", "yashrabalam9@gmail.com")
    assertEquals("Yash Rabalam", demoUser.displayName)
    assertEquals("yashrabalam9@gmail.com", demoUser.email)
    assertEquals("Google", demoUser.authProvider)
    org.junit.Assert.assertFalse(demoUser.isAnonymous)

    val guestUser = authManager.continueAsGuest()
    org.junit.Assert.assertTrue(guestUser.isAnonymous)
    assertEquals("Guest", guestUser.authProvider)
  }
}
