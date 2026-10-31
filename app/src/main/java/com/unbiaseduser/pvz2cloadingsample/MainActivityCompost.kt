package com.unbiaseduser.pvz2cloadingsample

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.unbiaseduser.pvz2cloading.compose.PvZ2CLoadingOverlay

class MainActivityCompost : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(topBar = { TopAppBar(title = { Text("PvZ2C Loading Sample (Compose)") }) } ) { paddingValues ->
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        var runLoadingAnimation: Boolean by remember { mutableStateOf(true) }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { runLoadingAnimation = false }) {
                                Text("Pause")
                            }
                            TextButton(onClick = { runLoadingAnimation = true }) {
                                Text("Resume")
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text("insert game content here", modifier = Modifier.fillMaxWidth())
                            PvZ2CLoadingOverlay(
                                running = runLoadingAnimation,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (isDark) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}