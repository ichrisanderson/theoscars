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
import com.chrisa.theoscars.features.movie.domain.LoadMovieDetailUseCase
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineDispatchers,
    private val coroutineScope: CloseableCoroutineScope,
    private val loadMovieDetailUseCase: LoadMovieDetailUseCase,
) : ViewModel(coroutineScope) {

    private val movieId: Long = checkNotNull(savedStateHandle["movieId"])

    private val _viewState = MutableStateFlow(ViewState.default())
    val viewState: StateFlow<ViewState> = _viewState

    init {
        loadMovie()
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
}

data class ViewState(
    val isLoading: Boolean,
    val movie: MovieDetailModel?,
) {

    companion object {
        fun default() = ViewState(
            isLoading = false,
            movie = null,
        )
    }
}
