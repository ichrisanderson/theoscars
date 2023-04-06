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
import com.chrisa.theoscars.features.movie.domain.DeleteWatchlistDataUseCase
import com.chrisa.theoscars.features.movie.domain.InsertWatchlistDataUseCase
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
    lateinit var updateWatchlistDataUseCase: InsertWatchlistDataUseCase

    @Inject
    lateinit var deleteWatchlistDataUseCase: DeleteWatchlistDataUseCase

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

    private fun deleteWatchlistItem(id: Long) {
        runBlocking {
            deleteWatchlistDataUseCase.execute(id)
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
            .also { deleteWatchlistItem(1L) }
            .assertEmptyWatchlistTextDisplayed()
    }

    @Test
    fun longClickEnablesSelectionMode() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also {
                updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 674324, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 615777, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 614934, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 595586, isOnWatchlist = true)
            }
            .longPressMovie(661374)
            .assertSelectionCountDisplayed(1)
            .assertRemoveAllFromWatchlistButtonDisplayed()
            .assertMovieSelected(661374, "Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun clickInSelectionModeSelectsMovie() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also {
                updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 674324, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 615777, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 614934, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 595586, isOnWatchlist = true)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .assertSelectionCountDisplayed(2)
            .assertMovieSelected(674324, "The Banshees of Inisherin")
    }

    @Test
    fun selectedMoviesRemovedFromWatchlist() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also {
                updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 674324, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 615777, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 614934, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 595586, isOnWatchlist = true)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickRemoveAllFromWatchlistButton()
            .assertEmptyWatchlistTextDisplayed()
            .assertMainAppBarDisplayed()
    }

    @Test
    fun selectionModeCancelled() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .also {
                updateWatchlistItem(id = 0L, movieId = 661374, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 674324, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 615777, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 614934, isOnWatchlist = true)
                updateWatchlistItem(id = 0L, movieId = 595586, isOnWatchlist = true)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickCloseButton()
            .assertMainAppBarDisplayed()
            .assertMovieNotSelected(674324, "The Banshees of Inisherin")
    }
}
