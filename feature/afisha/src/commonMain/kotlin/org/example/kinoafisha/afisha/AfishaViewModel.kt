package org.example.kinoafisha.afisha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.usecase.GetFeedUseCase
import org.koin.core.annotation.KoinViewModel

data class AfishaState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
)

@KoinViewModel
class AfishaViewModel(
    private val getFeed: GetFeedUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AfishaState(isLoading = true))
    val state: StateFlow<AfishaState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val page = getFeed()
                _state.update { it.copy(isLoading = false, movies = page.items) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки")
                }
            }
        }
    }
}
