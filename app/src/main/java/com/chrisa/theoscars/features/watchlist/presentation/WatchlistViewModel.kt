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

package com.chrisa.theoscars.features.watchlist.presentation

import androidx.lifecycle.ViewModel
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.features.watchlist.domain.WatchlistMoviesUseCase
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    coroutineScope: CloseableCoroutineScope,
    watchlistMoviesUseCase: WatchlistMoviesUseCase,
) : ViewModel(coroutineScope) {

    private val _viewState = MutableStateFlow(ViewState.default())
    val viewState: StateFlow<ViewState> = _viewState

    init {
        watchlistMoviesUseCase.execute()
            .onEach(::updateMovies)
            .launchIn(coroutineScope)
    }

    private fun updateMovies(movies: List<WatchlistMovieModel>) {
        _viewState.update { it.copy(movies = movies) }
    }
}

data class ViewState(
    val movies: List<WatchlistMovieModel>,
) {
    companion object {
        fun default() = ViewState(
            movies = emptyList(),
        )
    }
}
