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
class SearchScreenTest {

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
    fun searchBarIsDisplayed() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .assertSearchBarIsFocused()
    }

    @Test
    fun searchBarHasPlaceholderText() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .assertSearchBarHasPlaceholderText()
    }

    @Test
    fun assertCloseAction() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .clickCloseAction()
            .assertCloseAction()
    }

    @Test
    fun assertEmptyMovieText() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .assertEmptyMovieTextDisplayed()
    }

    @Test
    fun assertSearchResult() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .clearSearchTerm()
            .enterSearchTerm("Everything Everywhere")
            .hideKeyboard()
            .assertMovieDisplayed(
                movieId = 545611L,
                title = "Everything Everywhere All at Once",
                year = "2023",
            )
    }

    @Test
    fun assertSearchResultCleared() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .clearSearchTerm()
            .enterSearchTerm("Every")
            .hideKeyboard()
            .clickClearSearchButton()
            .assertEmptyMovieTextDisplayed()
    }

    @Test
    fun assertMovieClickAction() {
        SearchScreenRobot(composeTestRule)
            .setContent()
            .clearSearchTerm()
            .enterSearchTerm("Everything Everywhere")
            .hideKeyboard()
            .clickMovie(movieId = 545611L)
            .assertMovieClickAction(movieId = 545611L)
    }
}
