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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.presentation.MovieScreen
import com.chrisa.theoscars.features.movie.presentation.MovieViewModel
import com.chrisa.theoscars.util.KeyboardHelper
import com.chrisa.theoscars.util.getString
import com.chrisa.theoscars.util.waitOnAllNodesWitTag
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
                Surface(modifier = Modifier.statusBarsPadding()) {
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
        composeTestRule.waitOnAllNodesWitTag(playButtonTestTag)
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
        composeTestRule.waitOnAllNodesWitTag(tag)
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.title)))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.year.prefixWithCeremonyEmoji())))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.year.prefixWithCeremonyEmoji())))
        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(movie.overview)))

        composeTestRule.onNodeWithTag(tag)
            .assert(hasAnyDescendant(hasText(composeTestRule.getString(R.string.nominations_title))))
        movie.nominations.forEach { nomination ->
            composeTestRule.onNodeWithTag(tag)
                .assert(hasAnyDescendant(hasText(nomination.category, substring = true)))
            composeTestRule.onNodeWithTag(tag)
                .assert(hasAnyDescendant(hasText(nomination.name)))
        }
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
