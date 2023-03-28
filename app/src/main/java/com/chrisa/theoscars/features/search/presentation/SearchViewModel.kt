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

package com.chrisa.theoscars.features.search.presentation

import androidx.lifecycle.ViewModel
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.search.domain.SearchMoviesUseCase
import com.chrisa.theoscars.features.search.domain.models.SearchResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val coroutineScope: CloseableCoroutineScope,
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel(coroutineScope) {

    private val _viewState = MutableStateFlow(ViewState.default())
    val viewState: StateFlow<ViewState> = _viewState

    private var queryJob: Job? = null

    fun updateQuery(searchQuery: String) {
        queryJob?.cancel()
        _viewState.update { vs -> vs.copy(searchQuery = searchQuery) }
        queryJob = coroutineScope.launch(dispatchers.io) {
            val searchResults = searchMoviesUseCase.execute(searchQuery)
            _viewState.update { vs -> vs.copy(searchResults = searchResults) }
        }
    }

    fun clearQuery() {
        queryJob?.cancel()
        _viewState.update { vs -> vs.copy(searchQuery = "", searchResults = emptyList()) }
    }
}

data class ViewState(
    val searchQuery: String,
    val searchResults: List<SearchResultModel>,
) {

    companion object {
        fun default() = ViewState(
            searchQuery = "",
            searchResults = emptyList(),
        )
    }
}
