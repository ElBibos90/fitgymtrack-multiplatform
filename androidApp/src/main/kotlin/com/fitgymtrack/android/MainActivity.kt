package com.fitgymtrack.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fitgymtrack.android.ui.theme.FitGymTrackTheme
import com.fitgymtrack.platform.getWelcomeMessage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitGymTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Text(
        text = getWelcomeMessage() + "\n\n" +
                "✅ FASE 0 Completata!\n" +
                "✅ expect/actual funzionante!\n" +
                "✅ Shared module ok!\n" +
                "✅ Pronto per migrazione! 💪",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    FitGymTrackTheme {
        WelcomeScreen()
    }
}