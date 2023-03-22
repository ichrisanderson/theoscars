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

import com.chrisa.theoscars.core.data.db.category.CategoryDao
import com.chrisa.theoscars.core.data.db.movie.MovieDao

class FakeNominationDao(
    private val categoryDao: CategoryDao,
    private val movieDao: MovieDao,
) : NominationDao {

    private val nominations = mutableListOf<NominationEntity>()

    override fun countAll(): Int = nominations.size

    override fun insert(item: NominationEntity) {
        nominations.add(item)
    }

    override fun allCategoriesForCeremony(ceremonyYear: Int): List<String> {
        TODO("Not yet implemented")
    }

    override fun allNominationsCategoriesForMovie(movieId: Long): List<NominationCategory> {
        val categories = this.categoryDao.allCategories().associateBy { it.id }
        return this.nominations
            .filter { it.movieId == movieId }
            .map { nomination ->
                NominationCategory(
                    nomination = nomination.content,
                    category = categories[nomination.categoryId]!!.name,
                    winner = nomination.winner,
                )
            }
    }

    override fun allMoviesForCeremony(year: Int): List<MovieSummary> {
        val nominations = this.nominations
            .filter { it.year == year }
            .associateBy { it.movieId }
        val movies = movieDao.allMovies().filter { nominations.containsKey(it.id) }.map {
            MovieSummary(
                id = it.id,
                backdropImagePath = it.backdropImagePath.orEmpty(),
                title = it.title,
                overview = it.overview,
                year = nominations[it.id]!!.year,
            )
        }
        return movies
    }

    override fun allMoviesForCeremonyWithFilter(
        startYear: Int,
        endYear: Int,
        categories: List<Long>,
        genres: List<Long>,
    ): List<MovieSummary> {
        val categorySet = categories.toSet()
        val genresSet = genres.toSet()
        val allMovieGenres =
            movieDao.allMovieGenres().filter { genresSet.contains(it.genreId) }.map { it.movieId }
                .toSet()
        val nominations = this.nominations
            .filter { it.year in startYear..endYear && categorySet.contains(it.categoryId) }
            .associateBy { it.movieId }
        val movies = movieDao.allMovies().filter {
            nominations.containsKey(it.id) && allMovieGenres.contains(it.id)
        }.map {
            MovieSummary(
                id = it.id,
                backdropImagePath = it.backdropImagePath.orEmpty(),
                title = it.title,
                overview = it.overview,
                year = nominations[it.id]!!.year,
            )
        }
        return movies
    }

    override fun allNominationsForCeremony(ceremonyYear: Int): List<NominationEntity> {
        TODO("Not yet implemented")
    }

    override fun allContentForCeremony(ceremonyYear: Int): List<String> {
        TODO("Not yet implemented")
    }
}
