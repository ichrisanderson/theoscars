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

package com.chrisa.theoscars.features.watchlist

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
import com.chrisa.theoscars.features.movie.domain.UpdateWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class WatchlistScreenTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var initializeDataUseCase: InitializeDataUseCase

    @Inject
    lateinit var updateWatchlistDataUseCase: UpdateWatchlistDataUseCase

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            initializeDataUseCase.execute()
        }
    }

    private fun updateWatchlistItem(id: Long, movieId: Long, isOnWatchlist: Boolean) {
        runBlocking {
            updateWatchlistDataUseCase.execute(
                WatchlistDataModel(
                    id = id,
                    movieId = movieId,
                    isOnWatchlist = isOnWatchlist,
                    hasWatched = false,
                ),
            )
        }
    }

    @Test
    fun assertEmptyMovieText() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
    }

    @Test
    fun watchListItemAdded() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also { updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true) }
            .assertMovieTitleDisplayed("Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun watchListItemRemoved() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also { updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true) }
            .assertMovieTitleDisplayed("Glass Onion: A Knives Out Mystery")
            .also { updateWatchlistItem(id = 1L, movieId = 661374, isOnWatchlist = false) }
            .assertEmptyWatchlistTextDisplayed()
    }
}
