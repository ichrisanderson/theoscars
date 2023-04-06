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

import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.movie.data.MovieDataRepository
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoadWatchlistDataUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val movieDataRepository: MovieDataRepository,
) {
    fun execute(movieId: Long): Flow<WatchlistDataModel> =
        movieDataRepository.loadWatchlistData(movieId)
            .flowOn(coroutineDispatchers.io)
            .map {
                WatchlistDataModel(
                    id = it?.id ?: 0L,
                    movieId = it?.movieId ?: movieId,
                    isOnWatchlist = it != null,
                    hasWatched = it?.hasWatched ?: false,
                )
            }
}
