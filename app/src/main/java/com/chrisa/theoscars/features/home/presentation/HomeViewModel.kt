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

package com.chrisa.theoscars.features.home.presentation

import androidx.lifecycle.ViewModel
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
import com.chrisa.theoscars.features.home.domain.LoadMoviesUseCase
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dispatchers: CoroutineDispatchers,
    coroutineScope: CloseableCoroutineScope,
    private val initializeDataUseCase: InitializeDataUseCase,
    private val loadMoviesUseCase: LoadMoviesUseCase,
) : ViewModel(coroutineScope) {

    private val _viewState = MutableStateFlow(ViewState.default())
    val viewState: StateFlow<ViewState> = _viewState

    init {
        coroutineScope.launch(dispatchers.io) {
            _viewState.update { old -> old.copy(isLoading = true) }
            initializeDataUseCase.execute()
            loadMovies()
        }
    }

    private suspend fun loadMovies() {
        val movies = loadMoviesUseCase.execute()
        _viewState.update {
            it.copy(
                isLoading = false,
                movies = movies,
            )
        }
    }
}

data class ViewState(
    val isLoading: Boolean,
    val movies: List<MovieSummaryModel>,
) {

    companion object {
        fun default() = ViewState(
            isLoading = false,
            movies = emptyList(),
        )
    }
}
