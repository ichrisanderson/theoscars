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

import android.content.Context
import androidx.lifecycle.SavedStateHandle
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
import com.chrisa.theoscars.features.movie.data.MovieDataRepository
import com.chrisa.theoscars.features.movie.domain.LoadMovieDetailUseCase
import com.chrisa.theoscars.features.movie.domain.LoadWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.UpdateWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27])
class MovieViewModelTest {
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

        bootstrapper.insertData()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun movieViewModel(movieId: Long): MovieViewModel {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["movieId"] = movieId
        val repository = MovieDataRepository(appDatabase)
        return MovieViewModel(
            savedStateHandle = savedStateHandle,
            dispatchers = dispatchers,
            coroutineScope = CloseableCoroutineScope(),
            loadMovieDetailUseCase = LoadMovieDetailUseCase(
                coroutineDispatchers = dispatchers,
                movieDataRepository = repository,
            ),
            loadWatchlistDataUseCase = LoadWatchlistDataUseCase(
                coroutineDispatchers = dispatchers,
                movieDataRepository = repository,
            ),
            updateWatchlistDataUseCase = UpdateWatchlistDataUseCase(
                coroutineDispatchers = dispatchers,
                movieDataRepository = repository,
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN loading is complete`() {
        val sut = movieViewModel(49046)

        assertThat(sut.viewState.value.isLoading).isFalse()
    }

    @Test
    fun `WHEN initialised THEN movie summary displayed`() {
        val expectedMovie = MovieDetailModel(
            id = 49046,
            backdropImagePath = "/mqsPyyeDCBAghXyjbw4TfEYwljw.jpg",
            overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
            title = "All Quiet on the Western Front",
            year = "2023",
            youTubeVideoKey = "hf8EYbVxtCY",
            nominations = listOf(
                NominationModel(
                    category = "Cinematography",
                    name = "James Friend",
                    winner = false,
                ),
                NominationModel(
                    category = "International Feature Film",
                    name = "Germany",
                    winner = false,
                ),
                NominationModel(
                    category = "Makeup and Hairstyling",
                    name = "Heike Merker and Linda Eisenhamerová",
                    winner = false,
                ),
                NominationModel(
                    category = "Music (Original Score)",
                    name = "Volker Bertelmann",
                    winner = false,
                ),
                NominationModel(
                    category = "Best Picture",
                    name = "Malte Grunert, Producer",
                    winner = false,
                ),
                NominationModel(
                    category = "Production Design",
                    name = "Production Design: Christian M. Goldbeck; Set Decoration: Ernestine Hipper",
                    winner = false,
                ),
                NominationModel(
                    category = "Sound",
                    name = "Viktor Prášil, Frank Kruse, Markus Stemler, Lars Ginzel and Stefan Korte",
                    winner = false,
                ),
                NominationModel(
                    category = "Visual Effects",
                    name = "Frank Petzold, Viktor Müller, Markus Frank and Kamil Jafar",
                    winner = false,
                ),
                NominationModel(
                    category = "Writing (Adapted Screenplay)",
                    name = "Screenplay - Edward Berger, Lesley Paterson & Ian Stokell",
                    winner = false,
                ),
            ),
        )

        val sut = movieViewModel(expectedMovie.id)

        assertThat(sut.viewState.value.movie).isEqualTo(expectedMovie)
    }

    @Test
    fun `WHEN initialised THEN watchlist data uses defaults`() {
        val sut = movieViewModel(49046)

        assertThat(sut.viewState.value.watchlistData).isEqualTo(
            WatchlistDataModel(
                movieId = 49046,
                hasWatched = false,
                isOnWatchlist = false,
            ),
        )
    }

    @Test
    fun `WHEN toggle watched THEN watchlist data updated`() {
        val sut = movieViewModel(49046)

        sut.toggleWatched()

        assertThat(sut.viewState.value.watchlistData.hasWatched).isTrue()
    }

    @Test
    fun `WHEN toggle watchlist THEN watchlist data updated`() {
        val sut = movieViewModel(49046)

        sut.toggleWatchlist()

        assertThat(sut.viewState.value.watchlistData.isOnWatchlist).isTrue()
    }
}
