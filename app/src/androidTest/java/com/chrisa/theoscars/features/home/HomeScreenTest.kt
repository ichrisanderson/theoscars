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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
            .clickApplyButton()
            .assertMovieTitleDisplayed("The Banshees of Inisherin")
    }

    @Test
    fun winnersOnlyRowFilterItemsApplied() {
        HomeScreenRobot(composeTestRule)
            .setContent()
            .clickFilterButton()
            .clearStartYear()
            .enterStartYear("2022")
            .hideKeyboard()
            .clickWinnersOnlyRow()
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
            .hideKeyboard()
            .clickWinnersOnlySwitch()
            .clickApplyButton()
            .assertMovieTitleDisplayed("CODA")
    }
}
