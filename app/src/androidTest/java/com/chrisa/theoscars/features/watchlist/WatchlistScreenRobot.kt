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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.watchlist.presentation.WatchlistScreen
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.onNodeWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithStringResId
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
                    WatchlistScreen(
                        viewModel = hiltViewModel(),
                        onMovieClick = {
                            screenAction = ScreenAction.MovieClick(it)
                        },
                    )
                }
            }
        }
    }

    fun assertEmptyWatchlistTextDisplayed() = apply {
        composeTestRule.waitOnAllNodesWithStringResId(R.string.empty_watchlist_title)
        composeTestRule.onNodeWithStringResId(R.string.empty_watchlist_title)
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(composeTestRule.getString(R.string.empty_watchlist_icon_description))
            .assertIsDisplayed()
    }

    fun assertMovieTitleDisplayed(movieTitle: String) = apply {
        composeTestRule.waitOnAllNodesWithText(movieTitle)
        composeTestRule.onNodeWithText(movieTitle).assertIsDisplayed()
    }

    companion object {
        private const val searchBarTestTag = "searchBar"
        private const val searchBarPlaceholderTextTestTag = "searchBarPlaceholderText"
        private const val closeButtonTestTag = "closeButton"
        private const val clearSearchButtonTestTag = "clearSearchButton"
        private const val movieCardTestTag = "movieCard_"

        sealed class ScreenAction {
            data class MovieClick(val id: Long) : ScreenAction()
            object Close : ScreenAction()
        }
    }
}
