package org.example.kinoafisha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.kinoafisha.afisha.App
import org.example.kinoafisha.afisha.di.afishaModule
import org.example.kinoafisha.core.data.di.dataModule
import org.example.kinoafisha.core.database.di.databaseModule
import org.example.kinoafisha.core.domain.di.domainModule
import org.example.kinoafisha.core.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}