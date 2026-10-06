package org.example.kinoafisha.afisha

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.kinoafisha.core.domain.model.Movie
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AfishaScreen(viewModel: AfishaViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    AfishaContent(state = state, onRetry = viewModel::load)
}

@Composable
fun AfishaContent(
    state: AfishaState,
    onRetry: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ошибка: ${state.error}")
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Повторить")
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.movies) { movie ->
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview
@Composable
private fun AfishaContentPreview() {
    MaterialTheme {
        AfishaContent(
            state = AfishaState(
                movies = listOf(
                    Movie(1, "Дюна: Часть вторая", null),
                    Movie(2, "Оппенгеймер", null),
                ),
            ),
        )
    }
}
