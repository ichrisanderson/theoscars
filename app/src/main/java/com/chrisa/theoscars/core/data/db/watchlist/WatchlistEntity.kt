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

package com.chrisa.theoscars.core.data.db.watchlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.chrisa.theoscars.core.data.db.movie.MovieEntity

@Entity(
    tableName = "watchlist",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
        ),
    ],
)
data class WatchlistEntity(
    @PrimaryKey
    var movieId: Long,
    val isOnWatchlist: Boolean,
    val hasWatched: Boolean,
)
