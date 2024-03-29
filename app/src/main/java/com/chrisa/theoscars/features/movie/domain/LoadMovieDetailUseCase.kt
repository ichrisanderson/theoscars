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
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadMovieDetailUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val movieDataRepository: MovieDataRepository,
) {
    suspend fun execute(id: Long): MovieDetailModel = withContext(coroutineDispatchers.io) {
        val movie = movieDataRepository.loadMovie(id)
        val movieNominations = movieDataRepository.loadNominations(movie.id)

        val nominations = movieNominations.map {
            NominationModel(
                category = it.category,
                name = it.nomination,
                winner = it.winner,
            )
        }
        return@withContext MovieDetailModel(
            id = movie.id,
            backdropImagePath = movie.backdropImagePath,
            overview = movie.overview,
            title = movie.title,
            year = movieNominations.first().year.toString(10),
            youTubeVideoKey = movie.youTubeVideoKey,
            nominations = nominations,
        )
    }
}
