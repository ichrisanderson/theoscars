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
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.watchlist.domain.RemoveAllFromWatchlistUseCase
import com.chrisa.theoscars.features.watchlist.domain.SetAllAsUnwatchedUseCase
import com.chrisa.theoscars.features.watchlist.domain.SetAllAsWatchedUseCase
import com.chrisa.theoscars.features.watchlist.domain.WatchlistMoviesUseCase
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistModel
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val coroutineScope: CloseableCoroutineScope,
    watchlistMoviesUseCase: WatchlistMoviesUseCase,
    private val removeAllFromWatchlistUseCase: RemoveAllFromWatchlistUseCase,
    private val setAllAsWatchedUseCase: SetAllAsWatchedUseCase,
    private val setAllAsUnwatchedUseCase: SetAllAsUnwatchedUseCase,
) : ViewModel(coroutineScope) {

    private val _viewState = MutableStateFlow(ViewState.default())
    val viewState: StateFlow<ViewState> = _viewState

    init {
        watchlistMoviesUseCase.execute()
            .onEach(::updateMovies)
            .launchIn(coroutineScope)
    }

    private fun updateMovies(watchlist: WatchlistModel) {
        _viewState.update {
            it.copy(
                moviesToWatch = watchlist.moviesToWatch,
                moviesWatched = watchlist.moviesWatched,
            )
        }
    }

    fun toggleItemSelection(id: Long) {
        val ids = _viewState.value.selectedIds.toMutableSet()
        if (ids.contains(id)) {
            ids.remove(id)
        } else {
            ids.add(id)
        }
        _viewState.update { it.copy(selectedIds = ids) }
    }

    fun clearItemSelection() {
        _viewState.update { it.copy(selectedIds = emptySet()) }
    }

    fun removeSelectionFromWatchlist() {
        coroutineScope.launch(dispatchers.io) {
            removeAllFromWatchlistUseCase.execute(_viewState.value.selectedIds)
            _viewState.update { it.copy(selectedIds = emptySet()) }
        }
    }

    fun addSelectionToWatchedList() {
        coroutineScope.launch(dispatchers.io) {
            setAllAsWatchedUseCase.execute(_viewState.value.selectedIds)
            _viewState.update { it.copy(selectedIds = emptySet()) }
        }
    }

    fun removeSelectionFromWatchedList() {
        coroutineScope.launch(dispatchers.io) {
            setAllAsUnwatchedUseCase.execute(_viewState.value.selectedIds)
            _viewState.update { it.copy(selectedIds = emptySet()) }
        }
    }
}

data class ViewState(
    val moviesToWatch: List<WatchlistMovieModel>,
    val moviesWatched: List<WatchlistMovieModel>,
    val selectedIds: Set<Long>,
) {
    val hasSelectedIds = selectedIds.isNotEmpty()
    val selectedIdCount = selectedIds.size

    companion object {
        fun default() = ViewState(
            moviesToWatch = emptyList(),
            moviesWatched = emptyList(),
            selectedIds = emptySet(),
        )
    }
}
