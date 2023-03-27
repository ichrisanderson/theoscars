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

package com.chrisa.theoscars.features.search.presentation

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.Bootstrapper
import com.chrisa.theoscars.core.data.db.BootstrapperBuilder
import com.chrisa.theoscars.core.data.db.FakeAppDatabase
import com.chrisa.theoscars.core.data.db.FakeAssetFileManager
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.TestCoroutineDispatchersImpl
import com.chrisa.theoscars.features.search.data.SearchDataRepository
import com.chrisa.theoscars.features.search.domain.SearchMoviesUseCase
import com.chrisa.theoscars.features.search.domain.models.SearchResultModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestCoroutineDispatchersImpl(testDispatcher)

    private lateinit var appDatabase: AppDatabase
    private lateinit var bootstrapper: Bootstrapper

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        this.appDatabase = FakeAppDatabase()
        val assetManager = FakeAssetFileManager()

        this.bootstrapper = BootstrapperBuilder()
            .build(appDatabase, assetManager)

        bootstrapper.insertData()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun searchViewModel(): SearchViewModel {
        return SearchViewModel(
            dispatchers,
            CloseableCoroutineScope(),
            SearchMoviesUseCase(
                dispatchers,
                SearchDataRepository(appDatabase),
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN query is empty`() {
        val sut = searchViewModel()

        assertThat(sut.viewState.value.searchQuery).isEmpty()
    }

    @Test
    fun `WHEN initialised THEN results are empty`() {
        val sut = searchViewModel()

        assertThat(sut.viewState.value.searchResults).isEmpty()
    }

    @Test
    fun `WHEN query updated THEN matched results are returned`() {
        val sut = searchViewModel()

        sut.updateQuery("Everything Everywhere")

        assertThat(sut.viewState.value.searchResults).isEqualTo(
            listOf(
                SearchResultModel(
                    movieId = 545611,
                    title = "Everything Everywhere All at Once",
                    posterImagePath = "/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg",
                    year = "2023",
                ),
            ),
        )
    }
}
