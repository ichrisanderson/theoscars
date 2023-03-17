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
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadCategoriesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
) {
    suspend fun execute(): List<CategoryModel> = withContext(coroutineDispatchers.io) {
        return@withContext categoryModels
    }

    companion object {
        // TODO: Update these if adding more years
        private val categoryModels = listOf(
            // Feature films
            CategoryModel(name = "Best Picture", ids = listOf(48, 67, 127, 43, 8, 16)),
            CategoryModel(name = "International Feature Film", ids = listOf(9, 56, 111, 123)),
            CategoryModel(name = "Animated Feature Film", ids = listOf(106, 116)),
            CategoryModel(name = "Cinematography", ids = listOf(4, 117, 34, 35)),
            CategoryModel(name = "Documentary Feature Film", ids = listOf(120, 47, 44)),
            // Actors/Directors/Writers
            CategoryModel(name = "Actor in a Leading Role", ids = listOf(1, 87, 112)),
            CategoryModel(name = "Actress in a Leading Role", ids = listOf(2, 88, 114)),
            CategoryModel(name = "Actor in a Supporting Role", ids = listOf(27, 113)),
            CategoryModel(name = "Actress in a Supporting Role", ids = listOf(28, 115)),
            CategoryModel(name = "Directing", ids = listOf(25, 14, 119, 5, 6)),
            CategoryModel(
                name = "Writing",
                ids = listOf(
                    15,
                    10,
                    107,
                    133,
                    49,
                    46,
                    39,
                    134,
                    11,
                    84,
                    95,
                    103,
                    90,
                    96,
                    57,
                    62,
                    58,
                    26,
                    76,
                    74,
                    63,
                    53,
                    12,
                ),
            ),
            // Short movies
            CategoryModel(name = "Documentary Short Film", ids = listOf(40, 121)),
            CategoryModel(name = "Short Film (Animated)", ids = listOf(82, 129, 79, 18)),
            CategoryModel(
                name = "Short Film (Live Action)",
                ids = listOf(99, 83, 130, 29, 19, 61, 20, 30, 31),
            ),
            // Sound
            CategoryModel(
                name = "Sound",
                ids = listOf(
                    64,
                    131,
                    105,
                    68,
                    101,
                    108,
                    17,
                ),
            ),
            CategoryModel(
                name = "Music (Original Score)",
                ids = listOf(
                    94,
                    45,
                    41,
                    65,
                    77,
                    70,
                    104,
                    71,
                    33,
                    125,
                    72,
                    42,
                    66,
                    78,
                    81,
                    23,
                ),
            ),
            CategoryModel(
                name = "Music (Original Song)",
                ids = listOf(
                    97,
                    89,
                    102,
                    75,
                    85,
                    126,
                    73,
                    24,
                ),
            ),
            // Makeup/Costume
            CategoryModel(
                name = "Makeup and Hairstyling",
                ids = listOf(
                    100,
                    109,
                    124,
                ),
            ),
            CategoryModel(
                name = "Costume Design",
                ids = listOf(
                    60,
                    118,
                    50,
                    51,
                ),
            ),
            // Effects
            CategoryModel(
                name = "Production Design",
                ids = listOf(110, 128),
            ),
            CategoryModel(
                name = "Visual Effects",
                ids = listOf(7, 91, 132, 36, 52, 69),
            ),
            CategoryModel(
                name = "Film Editing",
                ids = listOf(22, 122),
            ),
        )
    }
}
