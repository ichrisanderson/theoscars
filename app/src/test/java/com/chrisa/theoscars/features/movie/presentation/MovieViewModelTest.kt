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
import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.Bootstrapper
import com.chrisa.theoscars.core.data.db.BootstrapperBuilder
import com.chrisa.theoscars.core.data.db.FakeAppDatabase
import com.chrisa.theoscars.core.data.db.FakeAssetFileManager
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.TestCoroutineDispatchersImpl
import com.chrisa.theoscars.features.movie.data.MovieDataRepository
import com.chrisa.theoscars.features.movie.domain.LoadMovieDetailUseCase
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {
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

        bootstrapper.insertData()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun movieViewModel(movieId: Long): MovieViewModel {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["movieId"] = movieId
        return MovieViewModel(
            savedStateHandle,
            dispatchers,
            CloseableCoroutineScope(),
            LoadMovieDetailUseCase(
                dispatchers,
                MovieDataRepository(appDatabase),
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
            youTubeVideoKey = "hf8EYbVxtCY",
            nominations = listOf(
                NominationModel(category = "Cinematography", name = "James Friend", winner = null),
                NominationModel(
                    category = "International Feature Film",
                    name = "Germany",
                    winner = null,
                ),
                NominationModel(
                    category = "Makeup and Hairstyling",
                    name = "Heike Merker and Linda Eisenhamerov??",
                    winner = null,
                ),
                NominationModel(
                    category = "Music (Original Score)",
                    name = "Volker Bertelmann",
                    winner = null,
                ),
                NominationModel(
                    category = "Best Picture",
                    name = "Malte Grunert, Producer",
                    winner = null,
                ),
                NominationModel(
                    category = "Production Design",
                    name = "Production Design: Christian M. Goldbeck; Set Decoration: Ernestine Hipper",
                    winner = null,
                ),
                NominationModel(
                    category = "Sound",
                    name = "Viktor Pr????il, Frank Kruse, Markus Stemler, Lars Ginzel and Stefan Korte",
                    winner = null,
                ),
                NominationModel(
                    category = "Visual Effects",
                    name = "Frank Petzold, Viktor M??ller, Markus Frank and Kamil Jafar",
                    winner = null,
                ),
                NominationModel(
                    category = "Writing (Adapted Screenplay)",
                    name = "Screenplay - Edward Berger, Lesley Paterson & Ian Stokell",
                    winner = null,
                ),
            ),
        )

        val sut = movieViewModel(expectedMovie.id)

        assertThat(sut.viewState.value.movie).isEqualTo(expectedMovie)
    }
}
