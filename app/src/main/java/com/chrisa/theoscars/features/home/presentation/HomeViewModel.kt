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
import timber.log.Timber
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
        Timber.tag("UI_TEST").d("Has Category: ${categories.any { it.name == "Best Picture" }}")
        _viewState.update {
            it.copy(
                isLoading = false,
                movies = movies,
                categories = categories,
                selectedCategory = categories.first(),
                genres = genres,
                selectedGenre = genres.first(),
                startYear = "2023",
                endYear = "2023",
            )
        }
    }

    fun updateFilter(filterModel: FilterModel) {
        _viewState.update { vs ->
            vs.copy(
                startYear = filterModel.startYearString,
                endYear = filterModel.endYearString,
                selectedCategory = filterModel.selectedCategory,
                selectedGenre = filterModel.selectedGenre,
            )
        }

        coroutineScope.launch(dispatchers.io) {
            _viewState.update { vs ->

                val filteredMovies = filterMoviesUseCase.execute(
                    startYear = filterModel.startYear,
                    endYear = filterModel.endYear,
                    selectedCategory = filterModel.selectedCategory,
                    selectedGenre = filterModel.selectedGenre,
                )

                vs.copy(movies = filteredMovies)
            }
        }
    }
}

data class ViewState(
    val isLoading: Boolean,
    val movies: List<MovieSummaryModel>,
    val categories: List<CategoryModel>,
    val selectedCategory: CategoryModel,
    val genres: List<GenreModel>,
    val selectedGenre: GenreModel,
    val startYear: String,
    val endYear: String,
) {

    companion object {
        fun default() = ViewState(
            isLoading = false,
            movies = emptyList(),
            categories = emptyList(),
            selectedCategory = CategoryModel(name = "", ids = emptyList()),
            genres = emptyList(),
            selectedGenre = GenreModel(name = "", ids = emptyList()),
            startYear = "",
            endYear = "",
        )
    }
}

data class FilterModel(
    val startYear: Int,
    val endYear: Int,
    val selectedCategory: CategoryModel,
    val selectedGenre: GenreModel,
) {
    val startYearString = startYear.toString(10)
    val endYearString = endYear.toString(10)
}
