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

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.Bootstrapper
import com.chrisa.theoscars.core.data.db.BootstrapperBuilder
import com.chrisa.theoscars.core.data.db.FakeAppDatabase
import com.chrisa.theoscars.core.data.db.FakeAssetFileManager
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.TestCoroutineDispatchersImpl
import com.chrisa.theoscars.features.home.data.HomeDataRepository
import com.chrisa.theoscars.features.home.domain.FilterMoviesUseCase
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
import com.chrisa.theoscars.features.home.domain.LoadCategoriesUseCase
import com.chrisa.theoscars.features.home.domain.LoadGenresUseCase
import com.chrisa.theoscars.features.home.domain.LoadMoviesUseCase
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestCoroutineDispatchersImpl(testDispatcher)

    private lateinit var appDatabase: AppDatabase
    private lateinit var bootstrapper: Bootstrapper

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        this.appDatabase = FakeAppDatabase()
        val assetManager = FakeAssetFileManager()

        this.bootstrapper = BootstrapperBuilder()
            .build(appDatabase, assetManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun homeViewModel(): HomeViewModel {
        val homeDataRepository = HomeDataRepository(appDatabase)
        return HomeViewModel(
            dispatchers = dispatchers,
            coroutineScope = CloseableCoroutineScope(),
            initializeDataUseCase = InitializeDataUseCase(
                dispatchers,
                bootstrapper,
            ),
            loadMoviesUseCase = LoadMoviesUseCase(
                dispatchers,
                homeDataRepository,
            ),
            filterMoviesUseCase = FilterMoviesUseCase(
                dispatchers,
                homeDataRepository,
            ),
            loadCategoriesUseCase = LoadCategoriesUseCase(
                dispatchers,
                homeDataRepository,
            ),
            loadGenresUseCase = LoadGenresUseCase(
                dispatchers,
                homeDataRepository,
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN movies are populated`() {
        val sut = homeViewModel()

        assertThat(appDatabase.movieDao().countAll()).isGreaterThan(0)
    }

    @Test
    fun `WHEN initialised THEN nominations are populated`() {
        val sut = homeViewModel()

        assertThat(appDatabase.nominationDao().countAll()).isGreaterThan(0)
    }

    @Test
    fun `WHEN initialised THEN movies are displayed in default order`() {
        val sut = homeViewModel()

        assertThat(sut.viewState.value.movies.first()).isEqualTo(
            MovieSummaryModel(
                id = 49046,
                backdropImagePath = "/mqsPyyeDCBAghXyjbw4TfEYwljw.jpg",
                overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
                title = "All Quiet on the Western Front",
                year = "2023",
            ),
        )
        assertThat(sut.viewState.value.movies.last()).isEqualTo(
            MovieSummaryModel(
                id = 1042171,
                backdropImagePath = "/572w5U3T7CiyAswyshiS48vO7uR.jpg",
                overview = "Ivalu is gone. Her little sister is desperate to find her and her father does not care. The vast Greenlandic nature holds secrets. Where is Ivalu?",
                title = "Ivalu",
                year = "2023",
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN loading is complete`() {
        val sut = homeViewModel()

        assertThat(sut.viewState.value.isLoading).isFalse()
    }

    @Test
    fun `WHEN initialised THEN all categories are selected`() {
        val sut = homeViewModel()

        assertThat(sut.viewState.value.selectedCategory.name).isEqualTo("All")
    }

    @Test
    fun `WHEN selected categories updated THEN viewState updated`() {
        val sut = homeViewModel()
        val selectedCategory =
            sut.viewState.value.categories.first { it.name == "Actor in a Leading Role" }
        val selectedGenre = sut.viewState.value.genres[0]

        sut.updateFilter(
            FilterModel(
                startYear = 2023,
                endYear = 2023,
                selectedCategory = selectedCategory,
                selectedGenre = selectedGenre,
            ),
        )

        assertThat(sut.viewState.value.selectedCategory.name).isEqualTo("Actor in a Leading Role")
    }

    @Test
    fun `WHEN category filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.categories
            .first { it.name == "Actress in a Supporting Role" }
        val selectedGenre = sut.viewState.value.genres.first()

        sut.updateFilter(
            FilterModel(
                startYear = 2023,
                endYear = 2023,
                selectedCategory = selectedCategory,
                selectedGenre = selectedGenre,
            ),
        )

        assertThat(sut.viewState.value.movies.map { it.title }).isEqualTo(
            listOf(
                "Black Panther: Wakanda Forever",
                "Everything Everywhere All at Once",
                "The Banshees of Inisherin",
                "The Whale",
            ),
        )
    }

    @Test
    fun `WHEN year filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.categories
            .first { it.name == "Best Picture" }
        val selectedGenre = sut.viewState.value.genres.first()

        sut.updateFilter(
            FilterModel(
                startYear = 1960,
                endYear = 1970,
                selectedCategory = selectedCategory,
                selectedGenre = selectedGenre,
            ),
        )

        assertThat(sut.viewState.value.movies.map { it.title }).isEqualTo(
            listOf(
                "The Sound of Music",
            ),
        )
    }

    @Test
    fun `GIVEN no movies match condition WHEN filter applied THEN viewState updated with empty movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.categories
            .first { it.name == "Writing" }
        val selectedGenre = sut.viewState.value.genres.first()

        sut.updateFilter(
            FilterModel(
                startYear = 1960,
                endYear = 1970,
                selectedCategory = selectedCategory,
                selectedGenre = selectedGenre,
            ),
        )

        assertThat(sut.viewState.value.movies).isEmpty()
    }

    @Test
    fun `WHEN genre filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.categories
            .first { it.name == "Best Picture" }
        val selectedGenre = sut.viewState.value.genres.first { it.name == "Science Fiction" }

        sut.updateFilter(
            FilterModel(
                startYear = 1960,
                endYear = 2023,
                selectedCategory = selectedCategory,
                selectedGenre = selectedGenre,
            ),
        )

        assertThat(sut.viewState.value.movies.map { it.title }).isEqualTo(
            listOf(
                "Avatar: The Way of Water",
                "Everything Everywhere All at Once",
            ),
        )
    }
}
