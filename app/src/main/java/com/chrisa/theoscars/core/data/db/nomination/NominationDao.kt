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

package com.chrisa.theoscars.core.data.db.nomination

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NominationDao {

    @Query("SELECT COUNT(year) FROM nomination")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: NominationEntity)

    @Query(
        "SELECT DISTINCT category.name FROM nomination " +
            "INNER JOIN category ON category.id = nomination.categoryId " +
            "WHERE year = :ceremonyYear",
    )
    fun allCategoriesForCeremony(ceremonyYear: Int): List<String>

    @Query(
        "SELECT DISTINCT nomination.content as 'nomination', category.name as 'category', nomination.winner FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "INNER JOIN category ON category.id = nomination.categoryId " +
            "WHERE movieId = :movieId",
    )
    fun allNominationsCategoriesForMovie(
        movieId: Long,
    ): List<NominationCategory>

    @Query(
        "SELECT DISTINCT movie.id, movie.backdropImagePath, movie.title, movie.overview, nomination.year FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "WHERE nomination.year = :year",
    )
    fun allMoviesForCeremony(
        year: Int,
    ): List<MovieSummary>

    @Query(
        "SELECT DISTINCT movie.id, movie.backdropImagePath, movie.title, movie.overview, nomination.year FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "INNER JOIN category ON category.id = nomination.categoryId " +
            "INNER JOIN movieGenre ON movieGenre.movieId = movie.id " +
            "WHERE nomination.year >= :startYear AND nomination.year <= :endYear AND category.id IN (:categories) AND movieGenre.genreId in (:genres)",
    )
    fun allMoviesForCeremonyWithFilter(
        startYear: Int,
        endYear: Int,
        categories: List<Long>,
        genres: List<Long>,
    ): List<MovieSummary>

    @Query("SELECT * FROM nomination WHERE year = :ceremonyYear")
    fun allNominationsForCeremony(ceremonyYear: Int): List<NominationEntity>

    @Query("SELECT DISTINCT content FROM nomination WHERE year = :ceremonyYear")
    fun allContentForCeremony(ceremonyYear: Int): List<String>
}

data class NominationCategory(
    val nomination: String,
    val category: String,
    val winner: Boolean,
)

data class MovieSummary(
    val id: Long,
    val backdropImagePath: String?,
    val title: String,
    val overview: String,
    val year: Int,
)
