package com.example.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.user.ui.UserMainScreen

/**
 * USER APP — Standalone Entry Point Activity for Customers
 *
 * Launches the customer food delivery interface:
 * Browse menu, customize meals, manage cart, place orders,
 * track real-time delivery progress, view order history,
 * and manage delivery addresses.
 */
class UserMainActivity : ComponentActivity() {
    private val viewModel: ZaykaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkThemeSetting by viewModel.isDarkTheme.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = isDarkThemeSetting ?: systemDark

            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UserMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
