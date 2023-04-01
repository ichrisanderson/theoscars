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

package com.chrisa.theoscars.features.movie.domain

import com.chrisa.theoscars.core.data.db.watchlist.WatchlistEntity
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.movie.data.MovieDataRepository
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateWatchlistDataUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val movieDataRepository: MovieDataRepository,
) {
    suspend fun execute(watchlistDataModel: WatchlistDataModel) = withContext(coroutineDispatchers.io) {
        movieDataRepository.insertWatchlistData(
            WatchlistEntity(
                movieId = watchlistDataModel.movieId,
                isOnWatchlist = watchlistDataModel.isOnWatchlist,
                hasWatched = watchlistDataModel.hasWatched,
            ),
        )
    }
}
