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
    lateinit var insertWatchlistDataUseCase: InsertWatchlistDataUseCase

    @Inject
    lateinit var deleteWatchlistDataUseCase: DeleteWatchlistDataUseCase

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            initializeDataUseCase.execute()
        }
    }

    private fun insertWatchlistItem(movieId: Long, hasWatched: Boolean = false) {
        runBlocking {
            insertWatchlistDataUseCase.execute(
                WatchlistDataModel(
                    id = 0L,
                    movieId = movieId,
                    hasWatched = hasWatched,
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
    fun assertEmptyWatchlistText() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
    }

    @Test
    fun watchListItemAdded() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also { insertWatchlistItem(movieId = 661374) }
            .assertMovieTitleDisplayed("Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun watchListItemRemoved() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also { insertWatchlistItem(movieId = 661374) }
            .assertMovieTitleDisplayed("Glass Onion: A Knives Out Mystery")
            .also { deleteWatchlistItem(1L) }
            .assertEmptyWatchlistTextDisplayed()
    }

    @Test
    fun longClickEnablesSelectionMode() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374)
                insertWatchlistItem(movieId = 674324)
                insertWatchlistItem(movieId = 615777)
                insertWatchlistItem(movieId = 614934)
                insertWatchlistItem(movieId = 595586)
            }
            .longPressMovie(661374)
            .assertSelectionCountDisplayed(1)
            .assertRemoveFromWatchlistButtonDisplayed()
            .assertMovieSelected(661374, "Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun clickInSelectionModeSelectsMovie() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374)
                insertWatchlistItem(movieId = 674324)
                insertWatchlistItem(movieId = 615777)
                insertWatchlistItem(movieId = 614934)
                insertWatchlistItem(movieId = 595586)
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
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374)
                insertWatchlistItem(movieId = 674324)
                insertWatchlistItem(movieId = 615777)
                insertWatchlistItem(movieId = 614934)
                insertWatchlistItem(movieId = 595586)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickRemoveAllFromWatchlistButton()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .assertMainAppBarDisplayed()
    }

    @Test
    fun selectedMoviesAddedToWatchedList() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374)
                insertWatchlistItem(movieId = 674324)
                insertWatchlistItem(movieId = 615777)
                insertWatchlistItem(movieId = 614934)
                insertWatchlistItem(movieId = 595586)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickAddToWatchedListButton()
            .scrollToWatchlistTitle()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextNotDisplayed()
            .assertMainAppBarDisplayed()
            .assertMovieNotSelected(661374, "Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun selectedMoviesKeptOnWatchedList() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374, hasWatched = false)
                insertWatchlistItem(movieId = 674324, hasWatched = true)
                insertWatchlistItem(movieId = 615777, hasWatched = true)
                insertWatchlistItem(movieId = 614934, hasWatched = true)
                insertWatchlistItem(movieId = 595586, hasWatched = true)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickAddToWatchedListButton()
            .scrollToWatchlistTitle()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextNotDisplayed()
            .assertMainAppBarDisplayed()
            .assertMovieNotSelected(661374, "Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun selectedMoviesRemovedFromWatchedList() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374, hasWatched = false)
                insertWatchlistItem(movieId = 674324, hasWatched = true)
                insertWatchlistItem(movieId = 615777, hasWatched = true)
                insertWatchlistItem(movieId = 614934, hasWatched = true)
                insertWatchlistItem(movieId = 595586, hasWatched = true)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickRemoveFromWatchedListButton()
            .scrollToWatchedListTitle()
            .assertEmptyWatchlistTextNotDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .assertMainAppBarDisplayed()
            .assertMovieNotSelected(661374, "Glass Onion: A Knives Out Mystery")
    }

    @Test
    fun selectionModeCancelled() {
        WatchlistScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyWatchlistTextDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .also {
                insertWatchlistItem(movieId = 661374)
                insertWatchlistItem(movieId = 674324)
                insertWatchlistItem(movieId = 615777)
                insertWatchlistItem(movieId = 614934)
                insertWatchlistItem(movieId = 595586)
            }
            .longPressMovie(661374)
            .clickMovie(674324)
            .clickMovie(615777)
            .clickMovie(614934)
            .clickMovie(595586)
            .clickCloseButton()
            .assertMainAppBarDisplayed()
            .assertEmptyWatchedListTextDisplayed()
            .assertMovieNotSelected(674324, "The Banshees of Inisherin")
    }
}
