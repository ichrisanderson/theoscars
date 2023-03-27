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

package com.chrisa.theoscars.features.home

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.home.presentation.HomeScreen
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.onAllNodesWithStringResId
import com.chrisa.theoscars.util.onNodeWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWitTag
import com.chrisa.theoscars.util.waitOnAllNodesWithText
import com.google.common.truth.Truth.assertThat

class HomeScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val keyboardHelper = KeyboardHelper(composeTestRule)
    private var screenAction: ScreenAction? = null

    fun setContent() = apply {
        composeTestRule.activity.setContent {
            keyboardHelper.initialize()
            OscarsTheme {
                Surface(modifier = Modifier.statusBarsPadding()) {
                    HomeScreen(
                        viewModel = hiltViewModel(),
                        onMovieClick = { screenAction = ScreenAction.MovieClick(it) },
                        onSearchClick = { screenAction = ScreenAction.Search },
                    )
                }
            }
        }
    }

    fun clickFilterButton() = apply {
        composeTestRule.waitOnAllNodesWitTag(filterButtonTestTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(filterButtonTestTag).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule
                .onAllNodesWithStringResId(R.string.filter_title)
                .fetchSemanticsNodes().size == 1
        }
    }

    fun waitForText(text: String) = apply {
        composeTestRule.waitOnAllNodesWithText(text)
    }

    fun assertFilterDialogIsVisible() = apply {
        composeTestRule.onNodeWithStringResId(R.string.filter_title).assertIsDisplayed()
        composeTestRule.onNodeWithStringResId(R.string.year_filter_title).assertIsDisplayed()
        composeTestRule.onNodeWithStringResId(R.string.apply_filter_cta).assertIsDisplayed()
    }

    fun clearStartYear() = apply {
        composeTestRule.onNodeWithTag(startYearTestTag).performTextClearance()
    }

    fun enterStartYear(year: String) = apply {
        composeTestRule.onNodeWithTag(startYearTestTag).performTextInput(year)
    }

    fun enterEndYear(year: String) = apply {
        composeTestRule.onNodeWithTag(endYearTestTag).performTextInput(year)
    }

    fun clearEndYear() = apply {
        composeTestRule.onNodeWithTag(endYearTestTag).performTextClearance()
    }

    fun hideKeyboard() = apply {
        keyboardHelper.hideKeyboardIfShown()
    }

    fun assertYearErrorIsDisplayed() = apply {
        composeTestRule.onNodeWithStringResId(R.string.year_filter_error).assertIsDisplayed()
    }

    fun assertYearErrorDoesNotExist() = apply {
        composeTestRule.onNodeWithStringResId(R.string.year_filter_error).assertDoesNotExist()
    }

    fun assertApplyButtonIsEnabled() = apply {
        composeTestRule.onNodeWithTag(applyButtonTestTag).assertIsEnabled()
    }

    fun assertApplyButtonIsDisabled() = apply {
        composeTestRule.onNodeWithTag(applyButtonTestTag).assertIsNotEnabled()
    }

    fun clickApplyButton() = apply {
        composeTestRule.onNodeWithTag(filterContentListTestTag)
            .performScrollToNode(hasTestTag(applyButtonTestTag))
        composeTestRule.onNodeWithTag(applyButtonTestTag).performClick()
    }

    fun clickCategory(categoryText: String) = apply {
        composeTestRule.onNodeWithTag(categoriesItemListTestTag).performScrollToKey(categoryText)
        composeTestRule.onNodeWithText(categoryText).performClick()
    }

    fun clickGenre(genreText: String) = apply {
        composeTestRule.onNodeWithTag(genresItemListTestTag).performScrollToKey(genreText)
        composeTestRule.onNodeWithText(genreText).performClick()
    }

    fun assertMovieTitleDisplayed(movieTitle: String) {
        composeTestRule.waitOnAllNodesWithText(movieTitle)
        composeTestRule.onNodeWithText(movieTitle).assertIsDisplayed()
    }

    fun clickWinnersOnlyRow() = apply {
        composeTestRule.onNodeWithTag(winnersOnlyRowTestTag).performClick()
    }

    fun clickWinnersOnlySwitch() = apply {
        composeTestRule.onNodeWithTag(winnersOnlySwitchTestTag).performClick()
    }

    fun clickSearchButton() = apply {
        composeTestRule.waitOnAllNodesWitTag(searchButtonTestTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(searchButtonTestTag).performClick()
    }

    fun assertSearchAction() = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.Search)
    }

    fun clickMovie(movieId: Long) = apply {
        composeTestRule.waitOnAllNodesWitTag(movieListTestTag)
        composeTestRule.onNodeWithTag(movieListTestTag).performScrollToKey(movieId)
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.onNodeWithTag(tag).performClick()
    }

    fun assertMovieClickAction(movieId: Long) = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.MovieClick(movieId))
    }

    companion object {
        private const val startYearTestTag = "startYear"
        private const val endYearTestTag = "endYear"
        private const val applyButtonTestTag = "applyButton"
        private const val filterButtonTestTag = "filterButton"
        private const val searchButtonTestTag = "searchButton"
        private const val movieListTestTag = "movieList"
        private const val movieCardTestTag = "movieCard_"
        private const val categoriesItemListTestTag = "itemRowFilter_Categories"
        private const val genresItemListTestTag = "itemRowFilter_Genres"
        private const val winnersOnlyRowTestTag = "winnersOnlyRow"
        private const val winnersOnlySwitchTestTag = "winnersOnlySwitch"
        private const val filterContentListTestTag = "filterContentList"

        sealed class ScreenAction {
            data class MovieClick(val id: Long) : ScreenAction()
            object Search : ScreenAction()
        }
    }
}
