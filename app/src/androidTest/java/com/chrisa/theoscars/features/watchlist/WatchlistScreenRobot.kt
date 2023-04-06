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

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performSemanticsAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.MainScreen
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.onNodeWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithTag
import com.chrisa.theoscars.util.waitOnAllNodesWithText

class WatchlistScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val keyboardHelper = KeyboardHelper(composeTestRule)
    private var screenAction: ScreenAction? = null

    fun setContent() = apply {
        composeTestRule.activity.setContent {
            keyboardHelper.initialize()
            OscarsTheme {
                Surface(modifier = Modifier.statusBarsPadding()) {
                    MainScreen(
                        watchlistViewModel = hiltViewModel(),
                        onMovieClick = { screenAction = ScreenAction.MovieClick(it) },
                        onSearchClick = { },
                    )
                }
            }
        }
        composeTestRule.onNodeWithStringResId(R.string.watchlist_tab)
            .performClick()
    }

    fun assertEmptyWatchlistTextDisplayed() = apply {
        composeTestRule.waitOnAllNodesWithStringResId(R.string.empty_watch_list_title)
        composeTestRule.onNodeWithStringResId(R.string.empty_watch_list_title)
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(composeTestRule.getString(R.string.empty_watch_list_icon_description))
            .assertIsDisplayed()
    }

    fun assertMovieTitleDisplayed(movieTitle: String) = apply {
        composeTestRule.waitOnAllNodesWithText(movieTitle)
        composeTestRule.onNodeWithText(movieTitle).assertIsDisplayed()
    }

    fun longPressMovie(movieId: Long) = apply {
        composeTestRule.waitOnAllNodesWithTag(watchlistItemsTestTag)
        composeTestRule.onNodeWithTag(watchlistItemsTestTag).performScrollToKey(movieId)
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.onNodeWithTag(tag)
            .performSemanticsAction(SemanticsActions.OnLongClick)
    }

    fun assertSelectionCountDisplayed(numberOfItemsSelected: Int) = apply {
        val selectionString = composeTestRule.getString(R.string.item_selection_title, numberOfItemsSelected)
        composeTestRule.waitOnAllNodesWithTag(selectionAppBarTestTag)
        composeTestRule.onNodeWithTag(selectionAppBarTestTag)
            .assert(hasAnyDescendant(hasText(selectionString)))
    }

    fun assertRemoveAllFromWatchlistButtonDisplayed() = apply {
        composeTestRule.onNodeWithTag(selectionAppBarTestTag)
            .assert(hasAnyDescendant(hasTestTag(removeAllFromWatchlistButtonTestTag)))
    }

    fun clickMovie(movieId: Long) = apply {
        composeTestRule.onNodeWithTag(watchlistItemsTestTag).performScrollToKey(movieId)
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.onNodeWithTag(tag)
            .performClick()
    }

    fun assertMovieSelected(movieId: Long, movieTitle: String) = apply {
        val movieSelectorTestTag = "$movieSelectedIndicatorTestTag$movieId"
        val selectionString = composeTestRule.getString(R.string.movie_selected_description_format, movieTitle)
        composeTestRule.waitOnAllNodesWithTag(movieSelectorTestTag, useUnmergedTree = true)
        composeTestRule.onNodeWithContentDescription(selectionString, useUnmergedTree = true).assertIsDisplayed()
    }

    fun clickRemoveAllFromWatchlistButton() = apply {
        composeTestRule.waitOnAllNodesWithTag(removeAllFromWatchlistButtonTestTag)
        composeTestRule.onNodeWithTag(removeAllFromWatchlistButtonTestTag)
            .performClick()
    }

    fun clickCloseButton() = apply {
        composeTestRule.onNodeWithTag(closeButtonTestTag)
            .performClick()
    }

    fun assertMainAppBarDisplayed() = apply {
        composeTestRule.onNodeWithTag(mainAppBarTestTag)
            .assertIsDisplayed()
    }

    fun assertMovieNotSelected(movieId: Long, movieTitle: String) = apply {
        val movieSelectorTestTag = "$movieSelectedIndicatorTestTag$movieId"
        val selectionString = composeTestRule.getString(R.string.movie_selected_description_format, movieTitle)
        composeTestRule.waitOnAllNodesWithTag(movieSelectorTestTag, true)
        composeTestRule.onNodeWithTag(movieSelectorTestTag, useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(selectionString, useUnmergedTree = true).assertDoesNotExist()
    }

    companion object {
        private const val watchlistItemsTestTag = "watchlistItems"
        private const val movieCardTestTag = "watchListMovieCard_"
        private const val movieSelectedIndicatorTestTag = "movieSelectedIndicator_"
        private const val selectionAppBarTestTag = "selectionAppBar"
        private const val mainAppBarTestTag = "mainAppBar"
        private const val closeButtonTestTag = "closeButton"
        private const val removeAllFromWatchlistButtonTestTag = "removeAllFromWatchlistButton"

        sealed class ScreenAction {
            data class MovieClick(val id: Long) : ScreenAction()
        }
    }
}
