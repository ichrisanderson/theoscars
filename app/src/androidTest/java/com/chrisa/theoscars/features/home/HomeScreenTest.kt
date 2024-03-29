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

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.chrisa.theoscars.MainActivity
import com.chrisa.theoscars.features.home.domain.InitializeDataUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var initializeDataUseCase: InitializeDataUseCase

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            initializeDataUseCase.execute()
        }
    }

    @Test
    fun filterIsDisplayed() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .assertFilterDialogIsVisible()
    }

    @Test
    fun startYearErrorMessageDisplayed() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearStartYear()
            .hideKeyboard()
            .assertYearErrorIsDisplayed()
            .assertApplyButtonIsDisabled()
    }

    @Test
    fun endYearErrorMessageDisplayed() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearEndYear()
            .hideKeyboard()
            .assertYearErrorIsDisplayed()
            .assertApplyButtonIsDisabled()
    }

    @Test
    fun applyButtonEnabledWhenValidDatesEntered() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearStartYear()
            .enterStartYear("1970")
            .clearEndYear()
            .enterEndYear("1999")
            .hideKeyboard()
            .assertYearErrorDoesNotExist()
            .assertApplyButtonIsEnabled()
    }

    @Test
    fun filterItemsApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .waitForText("Actor in a Leading Role")
            .clickCategory("Best Picture")
            .clickGenre("Comedy")
            .scrollToApplyButton()
            .clickApplyButton()
            .scrollToMovie(674324)
            .assertMovieTitleDisplayed("The Banshees of Inisherin")
    }

    @Test
    fun winnersOnlyRowFilterItemsApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearStartYear()
            .enterStartYear("2022")
            .clearEndYear()
            .enterEndYear("2022")
            .hideKeyboard()
            .clickWinnersOnlyRow()
            .scrollToApplyButton()
            .clickApplyButton()
            .assertMovieTitleDisplayed("CODA")
    }

    @Test
    fun winnersOnlySwitchFilterItemsApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearStartYear()
            .enterStartYear("2022")
            .clearEndYear()
            .enterEndYear("2022")
            .hideKeyboard()
            .clickWinnersOnlySwitch()
            .scrollToApplyButton()
            .clickApplyButton()
            .assertMovieTitleDisplayed("CODA")
    }

    @Test
    fun assertSearchAction() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickSearchButton()
            .assertSearchAction()
    }

    @Test
    fun assertMovieClickAction() {
        val movieId = 614934L
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickMovie(movieId)
            .assertMovieClickAction(movieId)
    }

    @Test
    fun assertWatchListToggle() {
        val movieId = 614934L
        HomeScreenRobot(composeTestRule)
            .setContent()
            .scrollToMovie(movieId)
            .assertRemovedFromWatchlistIconDisplayed(movieId)
            .clickAddToWatchlist(movieId)
            .assertAddedToWatchlistIconDislayed(movieId)
            .clickRemoveFromWatchlist(movieId)
            .assertRemovedFromWatchlistIconDisplayed(movieId)
    }

    @Test
    fun assertWatchedToggle() {
        val movieId = 614934L
        val expectedPercentageWatched = 1.9f // 1 movie / 54
        HomeScreenRobot(composeTestRule)
            .setContent()
            .scrollToMovie(movieId)
            .assertUnwatchedIconDisplayed(movieId)
            .assertWatchlistPercentageValue(0f)
            .clickMarkAsWatched(movieId)
            .assertWatchedIconDisplayed(movieId)
            .assertWatchlistPercentageValue(expectedPercentageWatched)
            .clickMarkAsUnwatched(movieId)
            .assertUnwatchedIconDisplayed(movieId)
            .assertWatchlistPercentageValue(0f)
    }

    @Test
    fun titleSortOrderApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickSortButton()
            .clickTitleText()
            .clickDescendingText()
            .clickApplyButton()
            .assertMovieTitleDisplayedAtPosition(0, "Women Talking")
            .clickSortButton()
            .clickTitleText()
            .clickAscendingText()
            .clickApplyButton()
            .assertMovieTitleDisplayedAtPosition(0, "A House Made of Splinters")
    }

    @Test
    fun yearSortOrderApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .assertMovieTitleDisplayedAtPosition(0, "A House Made of Splinters")
            .clickFilterButton()
            .clearStartYear()
            .enterStartYear("1960")
            .hideKeyboard()
            .scrollToApplyButton()
            .clickApplyButton()
            .clickSortButton()
            .clickYearText()
            .clickAscendingText()
            .clickApplyButton()
            .assertMovieTitleDisplayedAtPosition(0, "The Sound of Music")
            .clickSortButton()
            .clickYearText()
            .clickDescendingText()
            .clickApplyButton()
            .assertMovieTitleDisplayedAtPosition(0, "A House Made of Splinters")
    }
}
