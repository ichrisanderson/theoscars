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

import com.chrisa.theoscars.core.data.db.nomination.MovieSummary
import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.home.data.HomeDataRepository
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import com.chrisa.theoscars.features.home.domain.models.GenreModel
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import com.chrisa.theoscars.features.home.domain.models.SortDirection
import com.chrisa.theoscars.features.home.domain.models.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterMoviesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val homeDataRepository: HomeDataRepository,
) {
    fun execute(
        startYear: Int,
        endYear: Int,
        selectedCategory: CategoryModel,
        selectedGenre: GenreModel,
        winnersOnly: Boolean,
        sortOrder: SortOrder,
        sortDirection: SortDirection,
    ): Flow<List<MovieSummaryModel>> =
        homeDataRepository.allMoviesForCeremonyWithFilter(
            startYear = startYear,
            endYear = endYear,
            categoryAliasId = selectedCategory.id,
            genreId = selectedGenre.id,
            winner = if (winnersOnly) 1 else -1,
        )
            .flowOn(coroutineDispatchers.io)
            .map { items ->
                items.applySortOrder(sortOrder, sortDirection)
                    .map {
                        MovieSummaryModel(
                            id = it.id,
                            backdropImagePath = it.backdropImagePath,
                            title = it.title,
                            overview = it.overview,
                            year = it.year.toString(10),
                            watchlistId = it.watchlistId,
                            hasWatched = it.hasWatched ?: false,
                        )
                    }
            }

    private fun List<MovieSummary>.applySortOrder(sortOrder: SortOrder, sortDirection: SortDirection): List<MovieSummary> {
        return when {
            sortOrder == SortOrder.TITLE && sortDirection == SortDirection.ASCENDING -> this.sortedBy { it.title }
            sortOrder == SortOrder.TITLE && sortDirection == SortDirection.DESCENDING -> this.sortedByDescending { it.title }
            sortOrder == SortOrder.YEAR && sortDirection == SortDirection.ASCENDING -> this.sortedBy { it.year }
            sortOrder == SortOrder.YEAR && sortDirection == SortDirection.DESCENDING -> this.sortedByDescending { it.year }
            else -> this
        }
    }
}
