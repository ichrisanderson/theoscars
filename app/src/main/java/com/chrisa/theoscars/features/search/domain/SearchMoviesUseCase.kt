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

package com.chrisa.theoscars.features.search.domain

import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.search.data.SearchDataRepository
import com.chrisa.theoscars.features.search.domain.models.SearchResultModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val searchDataRepository: SearchDataRepository,
) {
    suspend fun execute(query: String): List<SearchResultModel> =
        withContext(coroutineDispatchers.io) {
            if (query.isEmpty()) return@withContext emptyList()
            val movies = searchDataRepository.searchMovies("%$query%")
            return@withContext movies.map {
                SearchResultModel(
                    movieId = it.id,
                    title = it.title,
                    posterImagePath = it.posterImagePath,
                    year = it.releaseYear.toString(10),
                )
            }
        }
}
