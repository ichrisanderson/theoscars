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

package com.chrisa.theoscars.features.movie.data

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.movie.MovieEntity
import com.chrisa.theoscars.core.data.db.nomination.NominationCategory
import com.chrisa.theoscars.core.data.db.watchlist.WatchlistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {

    fun loadMovie(id: Long): MovieEntity {
        val dao = appDatabase.movieDao()
        return dao.loadMovie(id)
    }

    fun loadNominations(movieId: Long): List<NominationCategory> {
        val dao = appDatabase.nominationDao()
        return dao.allNominationCategoriesForMovie(movieId)
    }

    fun loadWatchlistData(id: Long): Flow<WatchlistEntity?> {
        val dao = appDatabase.watchlistDao()
        return dao.loadWatchlistData(id)
    }

    fun insertWatchlistData(watchlistEntity: WatchlistEntity) {
        val dao = appDatabase.watchlistDao()
        return dao.insert(watchlistEntity)
    }

    fun deleteWatchlistData(watchListId: Long) {
        val dao = appDatabase.watchlistDao()
        return dao.delete(watchListId)
    }
}
