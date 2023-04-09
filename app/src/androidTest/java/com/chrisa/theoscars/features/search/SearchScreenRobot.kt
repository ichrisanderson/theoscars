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

package com.chrisa.theoscars.features.search

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.search.presentation.SearchScreen
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.onNodeWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithTag
import com.google.common.truth.Truth.assertThat

class SearchScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val keyboardHelper = KeyboardHelper(composeTestRule)
    private var screenAction: ScreenAction? = null

    fun setContent() = apply {
        composeTestRule.activity.setContent {
            keyboardHelper.initialize()
            OscarsTheme {
                Surface(modifier = Modifier.statusBarsPadding()) {
                    SearchScreen(
                        viewModel = hiltViewModel(),
                        onMovieClick = {
                            screenAction = ScreenAction.MovieClick(it)
                        },
                        onClose = {
                            screenAction = ScreenAction.Close
                        },
                    )
                }
            }
        }
    }

    fun hideKeyboard() = apply {
        keyboardHelper.hideKeyboardIfShown()
    }

    fun assertSearchBarIsFocused() = apply {
        composeTestRule.waitOnAllNodesWithTag(searchBarTestTag)
        composeTestRule.onNodeWithTag(searchBarTestTag).assertIsFocused()
    }

    fun assertSearchBarHasPlaceholderText() = apply {
        composeTestRule.waitOnAllNodesWithTag(
            searchBarPlaceholderTextTestTag,
            useUnmergedTree = true,
        )
        composeTestRule.onNodeWithTag(searchBarPlaceholderTextTestTag, useUnmergedTree = true)
            .assert(hasText(composeTestRule.getString(R.string.search_placeholder)))
    }

    fun clickCloseAction() = apply {
        composeTestRule.onNodeWithTag(closeButtonTestTag, useUnmergedTree = true)
            .performClick()
    }

    fun assertCloseAction() = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.Close)
    }

    fun assertEmptyMovieTextDisplayed() = apply {
        composeTestRule.onNodeWithTag("searchScreenContent").printToLog("UI_TEST")
        composeTestRule.waitOnAllNodesWithStringResId(R.string.empty_search_results_title)
        composeTestRule.onNodeWithStringResId(R.string.empty_search_results_title)
            .assertIsDisplayed()
        composeTestRule.onNodeWithStringResId(R.string.empty_search_results_subtitle)
            .assertIsDisplayed()
    }

    fun clearSearchTerm() = apply {
        composeTestRule.onNodeWithTag(searchBarTestTag).performTextClearance()
    }

    fun enterSearchTerm(year: String) = apply {
        composeTestRule.onNodeWithTag(searchBarTestTag).performTextInput(year)
    }

    fun assertMovieDisplayed(movieId: Long, title: String, year: String) {
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.waitOnAllNodesWithTag(tag)
        composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasText(title)))
        composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasText(year)))
    }

    fun clickClearSearchButton() = apply {
        composeTestRule.onNodeWithTag(clearSearchButtonTestTag, useUnmergedTree = true)
            .performClick()
    }

    fun clickMovie(movieId: Long) = apply {
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.waitOnAllNodesWithTag(tag)
        composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
            .performClick()
    }

    fun assertMovieClickAction(movieId: Long) = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.MovieClick(movieId))
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
