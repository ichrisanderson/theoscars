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
import com.chrisa.theoscars.features.home.domain.models.GenreModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadGenresUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val homeDataRepository: HomeDataRepository,
) {
    suspend fun execute(): List<GenreModel> = withContext(coroutineDispatchers.io) {
        val allGenres = homeDataRepository.allGenres()

        val allGenresModel = GenreModel(name = "All", id = 0)

        val result = mutableListOf<GenreModel>()
        result.add(allGenresModel)

        val mappedGenres = allGenres.map {
            GenreModel(
                name = it.name,
                id = it.id,
            )
        }
        result.addAll(mappedGenres)

        return@withContext result
    }
}
