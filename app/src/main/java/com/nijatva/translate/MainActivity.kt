package com.nijatva.translate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nijatva.translate.ui.NijatvaTranslateApp
import com.nijatva.translate.ui.TranslateViewModel
import com.nijatva.translate.ui.theme.NijatvaTranslateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedText = intent?.takeIf { it.action == Intent.ACTION_SEND }
            ?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            NijatvaTranslateTheme {
                val viewModel: TranslateViewModel = viewModel(
                    factory = TranslateViewModel.factory(applicationContext),
                )
                NijatvaTranslateApp(viewModel = viewModel, sharedText = sharedText)
            }
        }
    }
}
