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

package com.chrisa.theoscars.features.watchlist.data

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.nomination.MovieWatchlistSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchlistDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {
    fun watchlistMovies(): Flow<List<MovieWatchlistSummary>> {
        val dao = appDatabase.nominationDao()
        return dao.watchlistMovies()
    }

    fun removeAllFromWatchList(ids: Set<Long>) {
        val dao = appDatabase.watchlistDao()
        return dao.deleteAll(ids)
    }

    fun setAllAsWatched(ids: Set<Long>) {
        val dao = appDatabase.watchlistDao()
        return dao.setAllAsWatched(ids)
    }

    fun setAllAsUnwatched(ids: Set<Long>) {
        val dao = appDatabase.watchlistDao()
        return dao.setAllAsUnwatched(ids)
    }
}
