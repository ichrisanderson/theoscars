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

package com.chrisa.theoscars.features.watchlist.domain

import com.chrisa.theoscars.core.data.db.nomination.MovieWatchlistSummary
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.watchlist.data.WatchlistDataRepository
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistModel
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchlistMoviesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val repository: WatchlistDataRepository,
) {
    fun execute(): Flow<WatchlistModel> =
        repository.watchlistMovies()
            .flowOn(coroutineDispatchers.io)
            .map { items ->
                WatchlistModel(
                    moviesToWatch = items.filter { !it.hasWatched }.map(::mapWatchlistMovie),
                    moviesWatched = items.filter { it.hasWatched }.map(::mapWatchlistMovie),
                )
            }

    private fun mapWatchlistMovie(it: MovieWatchlistSummary) =
        WatchlistMovieModel(
            id = it.id,
            movieId = it.movieId,
            title = it.title,
            posterImagePath = it.posterImagePath,
            year = it.year.toString(10),
        )
}
