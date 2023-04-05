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

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chrisa.theoscars.core.data.db.AndroidAppDatabase
import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.Bootstrapper
import com.chrisa.theoscars.core.data.db.BootstrapperBuilder
import com.chrisa.theoscars.core.data.db.FakeAssetFileManager
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.TestCoroutineDispatchersImpl
import com.chrisa.theoscars.core.util.coroutines.TestExecutor
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27])
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestCoroutineDispatchersImpl(testDispatcher)

    private lateinit var appDatabase: AppDatabase
    private lateinit var bootstrapper: Bootstrapper

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AndroidAppDatabase::class.java)
            .setQueryExecutor(TestExecutor())
            .allowMainThreadQueries()
            .build()

        val assetManager = FakeAssetFileManager()

        this.bootstrapper = BootstrapperBuilder()
            .build(appDatabase, assetManager)
    }

    @After
    fun tearDown() {
        this.appDatabase.close()
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
                id = 614934,
                backdropImagePath = "/kaoTwHrJDmCPDLriN68pjlgx4R.jpg",
                overview = "The life story of Elvis Presley as seen through the complicated relationship with his enigmatic manager, Colonel Tom Parker.",
                title = "Elvis",
                year = "2023",
            ),
        )
        assertThat(sut.viewState.value.movies.last()).isEqualTo(
            MovieSummaryModel(
                id = 661374,
                backdropImagePath = "/dKqa850uvbNSCaQCV4Im1XlzEtQ.jpg",
                overview = "World-famous detective Benoit Blanc heads to Greece to peel back the layers of a mystery surrounding a tech billionaire and his eclectic crew of friends.",
                title = "Glass Onion: A Knives Out Mystery",
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

        assertThat(sut.viewState.value.filterModel.selectedCategory.name).isEqualTo("All")
    }

    @Test
    fun `WHEN selected categories updated THEN viewState updated`() {
        val sut = homeViewModel()
        val selectedCategory =
            sut.viewState.value.filterModel.categories.first { it.name == "Actor in a Leading Role" }

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
                selectedCategory = selectedCategory,
            ),
        )

        assertThat(sut.viewState.value.filterModel.selectedCategory.name).isEqualTo("Actor in a Leading Role")
    }

    @Test
    fun `WHEN category filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.filterModel.categories
            .first { it.name == "Actress in a Supporting Role" }

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
                selectedCategory = selectedCategory,
            ),
        )

        assertThat(sut.viewState.value.movies.map { it.title }).isEqualTo(
            listOf(
                "Black Panther: Wakanda Forever",
                "The Whale",
                "The Banshees of Inisherin",
                "Everything Everywhere All at Once",
            ),
        )
    }

    @Test
    fun `WHEN year filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.filterModel.categories
            .first { it.name == "Best Picture" }

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
                startYear = 1960,
                endYear = 1970,
                selectedCategory = selectedCategory,
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
        val selectedCategory = sut.viewState.value.filterModel.categories
            .first { it.name == "Writing" }

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
                startYear = 1960,
                endYear = 1970,
                selectedCategory = selectedCategory,
            ),
        )

        assertThat(sut.viewState.value.movies).isEmpty()
    }

    @Test
    fun `WHEN genre filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()
        val selectedCategory = sut.viewState.value.filterModel.categories
            .first { it.name == "Best Picture" }
        val selectedGenre = sut.viewState.value.filterModel.genres.first { it.name == "Science Fiction" }

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
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

    @Test
    fun `WHEN winner filter applied THEN viewState updated with selected movies`() {
        val sut = homeViewModel()

        sut.updateFilter(
            sut.viewState.value.filterModel.copy(
                startYear = 2021,
                endYear = 2023,
                winnersOnly = true,
            ),
        )

        assertThat(sut.viewState.value.movies.map { it.title }).isEqualTo(
            listOf(
                "Belfast",
            ),
        )
    }
}
