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

package com.chrisa.theoscars.features.home.domain

import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.home.data.HomeDataRepository
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadMoviesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val homeDataRepository: HomeDataRepository,
) {
    suspend fun execute(year: Int = 2023): List<MovieSummaryModel> = withContext(coroutineDispatchers.io) {
        homeDataRepository.allMoviesForCeremony(year)
            .map {
                MovieSummaryModel(
                    id = it.id,
                    backdropImagePath = it.backdropImagePath,
                    title = it.title,
                    overview = it.overview,
                    year = it.year.toString(10),
                )
            }
    }
}
