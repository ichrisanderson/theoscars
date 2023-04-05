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

package com.chrisa.theoscars.features.watchlist.presentation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chrisa.theoscars.core.data.db.AndroidAppDatabase
import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.Bootstrapper
import com.chrisa.theoscars.core.data.db.BootstrapperBuilder
import com.chrisa.theoscars.core.data.db.FakeAssetFileManager
import com.chrisa.theoscars.core.data.db.watchlist.WatchlistEntity
import com.chrisa.theoscars.core.util.coroutines.CloseableCoroutineScope
import com.chrisa.theoscars.core.util.coroutines.TestCoroutineDispatchersImpl
import com.chrisa.theoscars.core.util.coroutines.TestExecutor
import com.chrisa.theoscars.features.watchlist.data.WatchlistDataRepository
import com.chrisa.theoscars.features.watchlist.domain.RemoveFromWatchlistDataUseCase
import com.chrisa.theoscars.features.watchlist.domain.WatchlistMoviesUseCase
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27])
class WatchlistViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestCoroutineDispatchersImpl(testDispatcher)

    private lateinit var appDatabase: AppDatabase
    private lateinit var bootstrapper: Bootstrapper

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AndroidAppDatabase::class.java)
            .setQueryExecutor(TestExecutor())
            .allowMainThreadQueries()
            .build()

        val assetManager = FakeAssetFileManager()

        this.bootstrapper = BootstrapperBuilder()
            .build(appDatabase, assetManager)

        bootstrapper.insertData()
    }

    @After
    fun tearDown() {
        this.appDatabase.close()
        Dispatchers.resetMain()
    }

    private fun watchlistViewModel(): WatchlistViewModel {
        return WatchlistViewModel(
            dispatchers,
            CloseableCoroutineScope(),
            WatchlistMoviesUseCase(
                dispatchers,
                WatchlistDataRepository(appDatabase),
            ),
            RemoveFromWatchlistDataUseCase(
                dispatchers,
                WatchlistDataRepository(appDatabase),
            ),
        )
    }

    @Test
    fun `WHEN initialised THEN watchlist is empty`() {
        val sut = watchlistViewModel()

        assertThat(sut.viewState.value.movies).isEmpty()
    }

    @Test
    fun `WHEN item added to watchlist THEN watchlist movies updated`() {
        val sut = watchlistViewModel()

        appDatabase.watchlistDao().insert(
            WatchlistEntity(
                id = 0,
                movieId = 661374,
                isOnWatchlist = true,
                hasWatched = false,
            ),
        )

        assertThat(sut.viewState.value.movies).isEqualTo(
            listOf(
                WatchlistMovieModel(
                    id = 1,
                    movieId = 661374,
                    posterImagePath = "/vDGr1YdrlfbU9wxTOdpf3zChmv9.jpg",
                    title = "Glass Onion: A Knives Out Mystery",
                    year = "2023",
                ),
            ),
        )
    }

    @Test
    fun `WHEN item removed from watchlist THEN watchlist movies updated`() {
        val sut = watchlistViewModel()
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )

        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        appDatabase.watchlistDao().insert(entity.copy(id = 1, isOnWatchlist = false))

        assertThat(sut.viewState.value.movies).isEmpty()
    }

    @Test
    fun `WHEN viewmodel loaded THEN selection is empty`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()

        assertThat(sut.viewState.value.selectedIds).isEmpty()
    }

    @Test
    fun `WHEN item selected on watchlist THEN selected items updated`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()

        sut.toggleItemSelection(1)

        assertThat(sut.viewState.value.selectedIds).isEqualTo(setOf(1L))
    }

    @Test
    fun `WHEN item unselected from watchlist THEN selected items updated`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()
        sut.toggleItemSelection(1)

        sut.toggleItemSelection(1)

        assertThat(sut.viewState.value.selectedIds).isEmpty()
    }

    @Test
    fun `WHEN item selection cleared THEN selected items updated`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()
        sut.toggleItemSelection(1)

        sut.clearItemSelection()

        assertThat(sut.viewState.value.selectedIds).isEmpty()
    }

    @Test
    fun `WHEN item selection removed THEN selected items updated`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()
        sut.toggleItemSelection(1)

        sut.removeAllFromWatchlist()

        assertThat(sut.viewState.value.selectedIds).isEmpty()
    }

    @Test
    fun `WHEN item selection removed THEN watchlist updated`() {
        val entity = WatchlistEntity(
            id = 0,
            movieId = 661374,
            isOnWatchlist = false,
            hasWatched = false,
        )
        appDatabase.watchlistDao().insert(entity.copy(isOnWatchlist = true))
        val sut = watchlistViewModel()
        sut.toggleItemSelection(1)

        sut.removeAllFromWatchlist()

        assertThat(sut.viewState.value.movies).isEmpty()
    }
}
