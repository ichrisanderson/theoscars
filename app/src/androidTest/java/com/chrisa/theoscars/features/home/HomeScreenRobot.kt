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

import android.content.res.Resources
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.MainScreen
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.onAllNodesWithStringResId
import com.chrisa.theoscars.util.onNodeWithStringResId
import com.chrisa.theoscars.util.waitOnAllNodesWithMatcher
import com.chrisa.theoscars.util.waitOnAllNodesWithTag
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
                    MainScreen(
                        homeViewModel = hiltViewModel(),
                        onMovieClick = { screenAction = ScreenAction.MovieClick(it) },
                        onSearchClick = { screenAction = ScreenAction.Search },
                    )
                }
            }
        }
    }

    fun clickFilterButton() = apply {
        composeTestRule.waitOnAllNodesWithTag(filterButtonTestTag, useUnmergedTree = true)
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

    fun scrollToApplyButton() = apply {
        composeTestRule.onNodeWithTag(filterContentListTestTag)
            .performScrollToNode(hasTestTag(applyButtonTestTag))
    }

    fun clickApplyButton() = apply {
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

    fun assertMovieTitleDisplayed(movieTitle: String) = apply {
        composeTestRule.waitOnAllNodesWithText(movieTitle)
        composeTestRule.onNodeWithText(movieTitle).assertIsDisplayed()
    }

    fun assertMovieTitleDisplayedAtPosition(index: Int, movieTitle: String) = apply {
        composeTestRule.onNodeWithTag(movieListTestTag).performScrollToIndex(0)
        composeTestRule.onNodeWithTag(movieListTestTag)
            .onChildAt(index)
            .assert(hasText(movieTitle))
    }

    fun clickWinnersOnlyRow() = apply {
        composeTestRule.onNodeWithTag(winnersOnlyRowTestTag).performClick()
    }

    fun clickWinnersOnlySwitch() = apply {
        composeTestRule.onNodeWithTag(winnersOnlySwitchTestTag).performClick()
    }

    fun clickSearchButton() = apply {
        composeTestRule.waitOnAllNodesWithTag(searchButtonTestTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(searchButtonTestTag).performClick()
    }

    fun assertSearchAction() = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.Search)
    }

    fun scrollToMovie(movieId: Long) = apply {
        val offsetHeight = 48 * Resources.getSystem().displayMetrics.density // Extra scroll to avoid being covered by filter bar
        composeTestRule.waitOnAllNodesWithTag(movieListTestTag)
        composeTestRule.onNodeWithTag(movieListTestTag).performScrollToKey(movieId)
        composeTestRule.onNodeWithTag(movieListTestTag)
            .performSemanticsAction(SemanticsActions.ScrollBy) { it.invoke(0f, -offsetHeight) }
    }

    fun clickMovie(movieId: Long) = apply {
        composeTestRule.waitOnAllNodesWithTag(movieListTestTag)
        composeTestRule.onNodeWithTag(movieListTestTag).performScrollToKey(movieId)
        val tag = "$movieCardTestTag$movieId"
        composeTestRule.onNodeWithTag(tag).performClick()
    }

    fun assertMovieClickAction(movieId: Long) = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.MovieClick(movieId))
    }

    fun assertRemovedFromWatchlistIconDisplayed(movieId: Long) = apply {
        val addDescription = composeTestRule.getString(R.string.add_to_watchlist_icon_description)
        val removeDescription =
            composeTestRule.getString(R.string.remove_from_watchlist_icon_description)

        val tag = "${movieId}$watchActionsTestTag"
        composeTestRule.waitOnAllNodesWithTag(tag, useUnmergedTree = true)

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(addDescription),
            useUnmergedTree = true,
        ).assertIsDisplayed()

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(removeDescription),
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    fun clickAddToWatchlist(movieId: Long) = apply {
        val description = composeTestRule.getString(R.string.add_to_watchlist_icon_description)

        val tag = "${movieId}$watchActionsTestTag"
        composeTestRule.waitOnAllNodesWithMatcher(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        )

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        ).performClick()
    }

    fun assertAddedToWatchlistIconDislayed(movieId: Long) = apply {
        val addDescription = composeTestRule.getString(R.string.add_to_watchlist_icon_description)
        val removeDescription =
            composeTestRule.getString(R.string.remove_from_watchlist_icon_description)

        val tag = "${movieId}$watchActionsTestTag"
        composeTestRule.waitOnAllNodesWithTag(tag = tag, useUnmergedTree = true)

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(removeDescription),
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(addDescription),
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    fun clickRemoveFromWatchlist(movieId: Long) = apply {
        val description = composeTestRule.getString(R.string.remove_from_watchlist_icon_description)
        val tag = "${movieId}$watchActionsTestTag"

        composeTestRule.waitOnAllNodesWithMatcher(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        )

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        ).performClick()
    }

    fun assertUnwatchedIconDisplayed(movieId: Long) = apply {
        val markAsWatchedDescription =
            composeTestRule.getString(R.string.mark_as_watched_icon_description)
        val markAsUnwatchedDescription =
            composeTestRule.getString(R.string.mark_as_unwatched_icon_description)

        val tag = "${movieId}$watchActionsTestTag"
        composeTestRule.waitOnAllNodesWithTag(tag, useUnmergedTree = true)

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(
                markAsWatchedDescription,
            ),
            useUnmergedTree = true,
        ).assertIsDisplayed()

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(
                markAsUnwatchedDescription,
            ),
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    fun clickMarkAsWatched(movieId: Long) = apply {
        val description = composeTestRule.getString(R.string.mark_as_watched_icon_description)
        val tag = "${movieId}$watchActionsTestTag"

        composeTestRule.waitOnAllNodesWithMatcher(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        )

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        ).performClick()
    }

    fun assertWatchedIconDisplayed(movieId: Long) = apply {
        val markAsWatchedDescription =
            composeTestRule.getString(R.string.mark_as_watched_icon_description)
        val markAsUnwatchedDescription =
            composeTestRule.getString(R.string.mark_as_unwatched_icon_description)

        val tag = "${movieId}$watchActionsTestTag"
        composeTestRule.waitOnAllNodesWithTag(tag, useUnmergedTree = true)

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(
                markAsUnwatchedDescription,
            ),
            useUnmergedTree = true,
        ).assertIsDisplayed()

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(
                markAsWatchedDescription,
            ),
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    fun clickMarkAsUnwatched(movieId: Long) = apply {
        val description = composeTestRule.getString(R.string.mark_as_unwatched_icon_description)
        val tag = "${movieId}$watchActionsTestTag"

        composeTestRule.waitOnAllNodesWithMatcher(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        )

        composeTestRule.onNode(
            matcher = hasAnyAncestor(hasTestTag(tag)) and hasContentDescription(description),
            useUnmergedTree = true,
        ).performClick()
    }

    fun clickSortButton() = apply {
        composeTestRule.waitOnAllNodesWithTag(sortButtonTestTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(sortButtonTestTag).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule
                .onAllNodesWithStringResId(R.string.sort_title)
                .fetchSemanticsNodes().size == 1
        }
    }

    fun clickTitleText() = apply {
        composeTestRule.onNodeWithStringResId(R.string.title_label).performClick()
    }

    fun clickYearText() = apply {
        composeTestRule.onNodeWithStringResId(R.string.year_label).performClick()
    }

    fun clickDescendingText() = apply {
        composeTestRule.onNodeWithStringResId(R.string.descending_label).performClick()
    }

    fun clickAscendingText() = apply {
        composeTestRule.onNodeWithStringResId(R.string.ascending_label).performClick()
    }

    companion object {
        private const val startYearTestTag = "startYear"
        private const val endYearTestTag = "endYear"
        private const val applyButtonTestTag = "applyButton"
        private const val filterButtonTestTag = "filterButton"
        private const val sortButtonTestTag = "sortButton"
        private const val searchButtonTestTag = "searchButton"
        private const val movieListTestTag = "movieList"
        private const val movieCardTestTag = "movieCard_"
        private const val watchActionsTestTag = "_watchActions"
        private const val categoriesItemListTestTag = "filterList_Categories"
        private const val genresItemListTestTag = "filterList_Genres"
        private const val winnersOnlyRowTestTag = "winnersOnlyRow"
        private const val winnersOnlySwitchTestTag = "winnersOnlySwitch"
        private const val filterContentListTestTag = "filterContentList"

        sealed class ScreenAction {
            data class MovieClick(val id: Long) : ScreenAction()
            object Search : ScreenAction()
        }
    }
}
