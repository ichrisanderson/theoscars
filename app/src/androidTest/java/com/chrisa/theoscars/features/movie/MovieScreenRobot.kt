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

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import com.chrisa.theoscars.features.movie.presentation.MovieScreen
import com.chrisa.theoscars.features.movie.presentation.MovieViewModel
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.waitOnAllNodesWithContentDescription
import com.chrisa.theoscars.util.waitOnAllNodesWithTag
import com.google.common.truth.Truth.assertThat

class MovieScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val keyboardHelper = KeyboardHelper(composeTestRule)
    private var screenAction: ScreenAction? = null

    fun setContent(
        viewModel: MovieViewModel,
    ) = apply {
        composeTestRule.activity.setContent {
            keyboardHelper.initialize()
            OscarsTheme {
                Surface(
                    modifier = Modifier.statusBarsPadding()
                        .navigationBarsPadding(),
                ) {
                    MovieScreen(
                        viewModel = viewModel,
                        onPlayClicked = {
                            screenAction = ScreenAction.PlayClick(it)
                        },
                        onClose = {
                            screenAction = ScreenAction.Close
                        },
                    )
                }
            }
        }
    }

    fun clickCloseAction() = apply {
        composeTestRule.onNodeWithTag(closeButtonTestTag, useUnmergedTree = true)
            .performClick()
    }

    fun assertCloseAction() = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.Close)
    }

    fun clickPlayAction() = apply {
        composeTestRule.waitOnAllNodesWithTag(playButtonTestTag)
        composeTestRule.onNodeWithTag(playButtonTestTag)
            .performClick()
    }

    fun assertPlayAction(youtubeKey: String) = apply {
        assertThat(screenAction).isEqualTo(ScreenAction.PlayClick(youtubeKey))
    }

    fun assertMovieDisplayed(
        movie: MovieDetailModel,
    ) = apply {
        val tag = "$movieContentTestTag${movie.id}"
        composeTestRule.waitOnAllNodesWithTag(tag)
        assertMovieDetail(tag, movie)
        assertNominations(tag, movie.nominations)
    }

    private fun assertMovieDetail(
        tag: String,
        movie: MovieDetailModel,
    ) {
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.title)))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.year.prefixWithCeremonyEmoji())))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.year.prefixWithCeremonyEmoji())))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.overview)))
    }

    private fun assertNominations(
        tag: String,
        nominations: List<NominationModel>,
    ) {
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(composeTestRule.getString(R.string.nominations_title))))
        nominations.forEach { nomination ->
            composeTestRule.onNodeWithTag(tag)
                .assert(hasAnyDescendant(hasText(nomination.category, substring = true)))
            composeTestRule.onNodeWithTag(tag)
                .assert(hasAnyDescendant(hasText(nomination.name)))
        }
    }

    fun assertRemovedFromWatchlistIconDisplayed() = apply {
        val addDescription = composeTestRule.getString(R.string.add_to_watchlist_icon_description)
        val removeDescription =
            composeTestRule.getString(R.string.remove_from_watchlist_icon_description)
        composeTestRule.waitOnAllNodesWithContentDescription(addDescription)
        composeTestRule.onNodeWithContentDescription(removeDescription)
            .assertDoesNotExist()
    }

    fun clickAddToWatchlist() = apply {
        val description = composeTestRule.getString(R.string.add_to_watchlist_icon_description)
        composeTestRule.waitOnAllNodesWithContentDescription(description)
        composeTestRule.onNodeWithContentDescription(description)
            .performClick()
    }

    fun assertAddedToWatchlistIconDislayed() = apply {
        val addDescription = composeTestRule.getString(R.string.add_to_watchlist_icon_description)
        val removeDescription =
            composeTestRule.getString(R.string.remove_from_watchlist_icon_description)
        composeTestRule.waitOnAllNodesWithContentDescription(removeDescription)
        composeTestRule.onNodeWithContentDescription(addDescription)
            .assertDoesNotExist()
    }

    fun clickRemoveFromWatchlist() = apply {
        composeTestRule.onNodeWithContentDescription(composeTestRule.getString(R.string.remove_from_watchlist_icon_description))
            .performClick()
    }

    fun assertUnwatchedIconDisplayed() = apply {
        val markAsWatchedDescription =
            composeTestRule.getString(R.string.mark_as_watched_icon_description)
        val markAsUnwatchedDescription =
            composeTestRule.getString(R.string.mark_as_unwatched_icon_description)
        composeTestRule.waitOnAllNodesWithContentDescription(markAsWatchedDescription)
        composeTestRule.onNodeWithContentDescription(markAsUnwatchedDescription)
            .assertDoesNotExist()
    }

    fun clickMarkAsWatched() = apply {
        composeTestRule.onNodeWithContentDescription(composeTestRule.getString(R.string.mark_as_watched_icon_description))
            .performClick()
    }

    fun assertWatchedIconDisplayed() = apply {
        val markAsWatchedDescription =
            composeTestRule.getString(R.string.mark_as_watched_icon_description)
        val markAsUnwatchedDescription =
            composeTestRule.getString(R.string.mark_as_unwatched_icon_description)
        composeTestRule.waitOnAllNodesWithContentDescription(markAsUnwatchedDescription)
        composeTestRule.onNodeWithContentDescription(markAsWatchedDescription)
            .assertDoesNotExist()
    }

    fun clickMarkAsUnwatched() = apply {
        composeTestRule.onNodeWithContentDescription(composeTestRule.getString(R.string.mark_as_unwatched_icon_description))
            .performClick()
    }

    companion object {
        private const val closeButtonTestTag = "closeButton"
        private const val playButtonTestTag = "playButton"
        private const val movieContentTestTag = "movieContent_"

        sealed class ScreenAction {
            data class PlayClick(val youtubeKey: String) : ScreenAction()
            object Close : ScreenAction()
        }
    }
}
