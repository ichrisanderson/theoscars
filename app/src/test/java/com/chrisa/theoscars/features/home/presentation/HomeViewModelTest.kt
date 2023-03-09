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
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
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
        return HomeViewModel(
            dispatchers,
            CloseableCoroutineScope(),
            InitializeDataUseCase(
                dispatchers,
                bootstrapper,
            ),
            LoadMoviesUseCase(
                dispatchers,
                HomeDataRepository(appDatabase),
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
            ),
        )
        assertThat(sut.viewState.value.movies.last()).isEqualTo(
            MovieSummaryModel(
                id = 1043141,
                backdropImagePath = "/alfv8yO5jUS7HOsuu7v21hDUo1t.jpg",
                overview = "A cold night in December. Ebba waits for the tram to go home after a party, but the ride takes an unexpected turn.",
                title = "Night Ride",
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN loading is complete`() {
        val sut = homeViewModel()

        assertThat(sut.viewState.value.isLoading).isFalse()
    }
}
