package com.example

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
import com.example.ui.ZaykaMainScreen
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
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
          ZaykaMainScreen(viewModel = viewModel)
        }
      }
    }
  }
}

