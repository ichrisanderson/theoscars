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

package com.chrisa.theoscars.features.movie

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.movie.domain.LoadMovieDetailUseCase
import com.chrisa.theoscars.features.movie.domain.LoadWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.UpdateWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.presentation.MovieViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MovieScreenTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dispatchers: CoroutineDispatchers

    @Inject
    lateinit var coroutineScope: CloseableCoroutineScope

    @Inject
    lateinit var loadMovieDetailUseCase: LoadMovieDetailUseCase

    @Inject
    lateinit var loadWatchlistDataUseCase: LoadWatchlistDataUseCase

    @Inject
    lateinit var updateWatchlistDataUseCase: UpdateWatchlistDataUseCase

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var movie: MovieDetailModel

    @Before
    fun setup() {
        hiltRule.inject()
        savedStateHandle = SavedStateHandle()
        savedStateHandle["movieId"] = movieId
        runBlocking {
            movie = loadMovieDetailUseCase.execute(movieId)
        }
    }

    private fun movieViewModel() =
        MovieViewModel(
            savedStateHandle = savedStateHandle,
            dispatchers = dispatchers,
            coroutineScope = coroutineScope,
            loadMovieDetailUseCase = loadMovieDetailUseCase,
            loadWatchlistDataUseCase = loadWatchlistDataUseCase,
            updateWatchlistDataUseCase = updateWatchlistDataUseCase,
        )

    @Test
    fun assertCloseAction() {
        MovieScreenRobot(composeTestRule)
            .setContent(movieViewModel())
            .clickCloseAction()
            .assertCloseAction()
    }

    @Test
    fun assertPlayAction() {
        MovieScreenRobot(composeTestRule)
            .setContent(movieViewModel())
            .clickPlayAction()
            .assertPlayAction(movie.youTubeVideoKey!!)
    }

    @Test
    fun assertMovieDetail() {
        MovieScreenRobot(composeTestRule)
            .setContent(movieViewModel())
            .assertMovieDisplayed(movie)
    }

    companion object {
        private const val movieId = 545611L
    }
}
