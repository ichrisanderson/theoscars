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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    @Query("SELECT COUNT(id) FROM movie")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: MovieEntity)

    @Query("SELECT * FROM movie WHERE id = :id LIMIT 1")
    fun loadMovie(id: Long): MovieEntity?

    @Query("SELECT * FROM movie")
    fun allMovies(): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE metadata LIKE :query")
    fun searchMovies(query: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieGenres(items: List<MovieGenreEntity>)

    @Query("SELECT * FROM movieGenre")
    fun allMovieGenres(): List<MovieGenreEntity>
}
