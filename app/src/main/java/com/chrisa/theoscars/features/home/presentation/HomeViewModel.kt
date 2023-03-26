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
import com.chrisa.theoscars.features.home.domain.FilterMoviesUseCase
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
import com.chrisa.theoscars.features.home.domain.LoadCategoriesUseCase
import com.chrisa.theoscars.features.home.domain.LoadGenresUseCase
import com.chrisa.theoscars.features.home.domain.LoadMoviesUseCase
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import com.chrisa.theoscars.features.home.domain.models.GenreModel
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val coroutineScope: CloseableCoroutineScope,
    private val initializeDataUseCase: InitializeDataUseCase,
    private val loadMoviesUseCase: LoadMoviesUseCase,
    private val filterMoviesUseCase: FilterMoviesUseCase,
    private val loadCategoriesUseCase: LoadCategoriesUseCase,
    private val loadGenresUseCase: LoadGenresUseCase,
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
        val categories = loadCategoriesUseCase.execute()
        val genres = loadGenresUseCase.execute()
        _viewState.update {
            it.copy(
                isLoading = false,
                movies = movies,
                filterModel = FilterModel(
                    categories = categories,
                    selectedCategory = categories.first(),
                    genres = genres,
                    selectedGenre = genres.first(),
                    startYear = 2023,
                    endYear = 2023,
                    winnersOnly = false,
                ),
            )
        }
    }

    fun updateFilter(filterModel: FilterModel) {
        _viewState.update { vs -> vs.copy(filterModel = filterModel) }

        coroutineScope.launch(dispatchers.io) {
            _viewState.update { vs ->
                val filteredMovies = filterMoviesUseCase.execute(
                    startYear = filterModel.startYear,
                    endYear = filterModel.endYear,
                    selectedCategory = filterModel.selectedCategory,
                    selectedGenre = filterModel.selectedGenre,
                    winnersOnly = filterModel.winnersOnly,
                )
                vs.copy(movies = filteredMovies)
            }
        }
    }
}

data class ViewState(
    val isLoading: Boolean,
    val movies: List<MovieSummaryModel>,
    val filterModel: FilterModel,
) {

    companion object {
        fun default() = ViewState(
            isLoading = true,
            movies = emptyList(),
            filterModel = FilterModel.default(),
        )
    }
}

data class FilterModel(
    val startYear: Int,
    val endYear: Int,
    val categories: List<CategoryModel>,
    val selectedCategory: CategoryModel,
    val genres: List<GenreModel>,
    val selectedGenre: GenreModel,
    val winnersOnly: Boolean,
) {
    val startYearString = startYear.toString(10)
    val endYearString = endYear.toString(10)

    companion object {
        fun default() = FilterModel(
            categories = emptyList(),
            selectedCategory = CategoryModel(name = "", id = -1L),
            genres = emptyList(),
            selectedGenre = GenreModel(name = "", id = -1L),
            startYear = 0,
            endYear = 0,
            winnersOnly = false,
        )
    }
}
