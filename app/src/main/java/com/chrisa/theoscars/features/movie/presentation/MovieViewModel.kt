/*
 * Copyright 2023 Chris Anderson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisa.theoscars.features.movie.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.movie.domain.DeleteWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.InsertWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.LoadMovieDetailUseCase
import com.chrisa.theoscars.features.movie.domain.LoadWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineDispatchers,
    private val coroutineScope: CloseableCoroutineScope,
    private val loadMovieDetailUseCase: LoadMovieDetailUseCase,
    private val loadWatchlistDataUseCase: LoadWatchlistDataUseCase,
    private val insertWatchlistDataUseCase: InsertWatchlistDataUseCase,
    private val deleteWatchlistDataUseCase: DeleteWatchlistDataUseCase,
) : ViewModel(coroutineScope) {

    private val movieId: Long = checkNotNull(
        savedStateHandle["movieId"],
    )

    private val _viewState = MutableStateFlow(MovieViewState.default())
    val viewState: StateFlow<MovieViewState> = _viewState

    init {
        loadMovie()
        subscribeToWatchListUpdates()
    }

    private fun loadMovie() {
        coroutineScope.launch(dispatchers.io) {
            _viewState.update { it.copy(isLoading = true) }
            val movieData = loadMovieDetailUseCase.execute(movieId)
            _viewState.update {
                it.copy(
                    isLoading = false,
                    movie = movieData,
                )
            }
        }
    }

    private fun subscribeToWatchListUpdates() {
        loadWatchlistDataUseCase.execute(movieId)
            .distinctUntilChanged()
            .onEach(::updateWatchlistDataModel)
            .launchIn(coroutineScope)
    }

    private fun updateWatchlistDataModel(watchlistData: WatchlistDataModel) {
        _viewState.update { it.copy(watchlistData = watchlistData) }
    }

    fun toggleWatchlist() {
        coroutineScope.launch(dispatchers.io) {
            val watchlistData = _viewState.value.watchlistData
            if (watchlistData.isOnWatchlist) {
                deleteWatchlistDataUseCase.execute(watchlistData.id)
            } else {
                insertWatchlistDataUseCase.execute(watchlistData.id, watchlistData.movieId, false)
            }
        }
    }

    fun toggleWatched() {
        coroutineScope.launch(dispatchers.io) {
            val watchlistData = _viewState.value.watchlistData
            insertWatchlistDataUseCase.execute(watchlistData.id, watchlistData.movieId, !watchlistData.hasWatched)
        }
    }
}

data class MovieViewState(
    val isLoading: Boolean,
    val movie: MovieDetailModel,
    val watchlistData: WatchlistDataModel,
) {

    companion object {
        fun default() = MovieViewState(
            isLoading = false,
            movie = MovieDetailModel(
                id = 0L,
                backdropImagePath = null,
                overview = "",
                title = "",
                year = "",
                youTubeVideoKey = null,
                nominations = emptyList(),
            ),
            watchlistData = WatchlistDataModel(
                id = 0L,
                movieId = 0L,
                hasWatched = false,
            ),
        )
    }
}
