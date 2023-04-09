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
import kotlinx.coroutines.flow.Flow

@Dao
interface NominationDao {

    @Query("SELECT COUNT(year) FROM nomination")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: NominationEntity)

    @Query(
        "SELECT DISTINCT nomination.content as 'nomination', category.name as 'category', nomination.winner, nomination.year FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "INNER JOIN category ON category.id = nomination.categoryId " +
            "WHERE movieId = :movieId",
    )
    fun allNominationCategoriesForMovie(
        movieId: Long,
    ): List<NominationCategory>

    @Query(
        "SELECT DISTINCT movie.id, movie.backdropImagePath, movie.title, movie.overview, nomination.year, watchlist.id as watchlistId, watchlist.hasWatched FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "INNER JOIN category ON category.id = nomination.categoryId " +
            "INNER JOIN categoryAlias ON category.categoryAliasId = categoryAlias.id " +
            "LEFT OUTER JOIN movieGenre ON movieGenre.movieId = movie.id " +
            "LEFT OUTER JOIN watchlist ON watchlist.movieId = movie.id " +
            "WHERE (nomination.year >= :startYear AND nomination.year <= :endYear) AND (:categoryAliasId = 0 OR categoryAlias.id = :categoryAliasId) AND (:genreId = 0 OR movieGenre.genreId = :genreId) AND (:winner = -1 OR nomination.winner = :winner)",
    )
    fun allMoviesForCeremonyWithFilter(
        startYear: Int,
        endYear: Int,
        categoryAliasId: Long,
        genreId: Long,
        winner: Int,
    ): Flow<List<MovieSummary>>

    @Query(
        "SELECT DISTINCT movie.id, movie.posterImagePath, movie.title, movie.overview, nomination.year FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "WHERE movie.metadata LIKE :query",
    )
    fun searchMovies(
        query: String,
    ): List<MovieSearchSummary>

    @Query(
        "SELECT DISTINCT watchlist.id, watchlist.movieId, movie.posterImagePath, movie.title, movie.overview, nomination.year, watchlist.hasWatched FROM nomination " +
            "INNER JOIN movie ON movie.id = nomination.movieId " +
            "INNER JOIN watchlist ON watchlist.movieId = movie.id ",
    )
    fun watchlistMovies(): Flow<List<MovieWatchlistSummary>>
}

data class NominationCategory(
    val nomination: String,
    val category: String,
    val winner: Boolean,
    val year: Int,
)

data class MovieSummary(
    val id: Long,
    val backdropImagePath: String?,
    val title: String,
    val overview: String,
    val year: Int,
    val watchlistId: Long?,
    val hasWatched: Boolean?,
)

data class MovieSearchSummary(
    val id: Long,
    val posterImagePath: String?,
    val title: String,
    val year: Int,
)

data class MovieWatchlistSummary(
    val id: Long,
    val movieId: Long,
    val posterImagePath: String?,
    val title: String,
    val year: Int,
    val hasWatched: Boolean,
)
