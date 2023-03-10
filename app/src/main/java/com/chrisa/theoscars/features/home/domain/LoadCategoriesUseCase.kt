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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadCategoriesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
) {
    suspend fun execute(): List<String> = withContext(coroutineDispatchers.io) {
        return@withContext categoryOrderMap
    }

    companion object {
        // TODO: Update these if adding more years
        private val categoryOrderMap = listOf(
            // Feature films
            "Best Picture",
            "International Feature Film",
            "Animated Feature Film",
            "Cinematography",
            "Documentary Feature Film",
            // Actors/Directors/Writers
            "Actor in a Leading Role",
            "Actress in a Leading Role",
            "Actor in a Supporting Role",
            "Actress in a Supporting Role",
            "Directing",
            "Writing (Adapted Screenplay)",
            "Writing (Original Screenplay)",
            // Short movies
            "Documentary Short Film",
            "Short Film (Animated)",
            "Short Film (Live Action)",
            // Sound
            "Sound",
            "Music (Original Score)",
            "Music (Original Song)",
            // Makeup/Costume
            "Makeup and Hairstyling",
            "Costume Design",
            // Effects
            "Production Design",
            "Visual Effects",
            "Film Editing"
        )
    }
}
