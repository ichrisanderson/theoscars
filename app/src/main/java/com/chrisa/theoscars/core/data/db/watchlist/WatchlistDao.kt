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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(watchlist: WatchlistEntity)

    @Query("SELECT * FROM watchlist WHERE movieId = :movieId LIMIT 1")
    fun loadWatchlistData(movieId: Long): Flow<WatchlistEntity?>

    @Query("DELETE FROM watchlist WHERE id IN (:ids)")
    fun deleteAll(ids: Set<Long>)

    @Query("DELETE FROM watchlist WHERE id = :id")
    fun delete(id: Long)

    @Query("UPDATE watchlist SET hasWatched = 1 WHERE id IN (:ids)")
    fun setAllAsWatched(ids: Set<Long>)

    @Query("UPDATE watchlist SET hasWatched = 0 WHERE id IN (:ids)")
    fun setAllAsUnwatched(ids: Set<Long>)
}
