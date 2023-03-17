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

package com.chrisa.theoscars.core.data.db.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie",
)
data class MovieEntity(
    @PrimaryKey
    val id: Long,
    val backdropImagePath: String?,
    val posterImagePath: String?,
    val overview: String,
    val title: String,
    val releaseYear: Int,
    val youTubeVideoKey: String?,
    val imdbId: String? = null,
    val originalLanguage: String? = null,
    val spokenLanguages: String? = null,
    val originalTitle: String? = null,
    val displayTitle: String? = null,
    val metadata: String? = null,
    val runtime: Int? = null,
    val isTvMovie: Boolean = false,
)
