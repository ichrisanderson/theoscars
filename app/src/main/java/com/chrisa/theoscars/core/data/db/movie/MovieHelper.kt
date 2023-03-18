/*
 * Copyright 2021 Chris Anderson.
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

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.LocalDateConverter
import javax.inject.Inject

class MovieHelper @Inject constructor(
    appDatabase: AppDatabase,
    private val dataSource: MovieDataSource,
    private val localDateConverter: LocalDateConverter,
) {
    private val movieDao = appDatabase.movieDao()

    fun insertData() {
        val items = movieDao.countAll()
        if (items > 0) return

        val dataSourceItems = dataSource.getMovies()

        dataSourceItems.forEach { movie ->
            movieDao.insert(
                MovieEntity(
                    id = movie.id,
                    backdropImagePath = movie.backdropImagePath,
                    posterImagePath = movie.posterImagePath,
                    overview = movie.overview,
                    title = movie.title,
                    releaseYear = movie.releaseYear,
                    youTubeVideoKey = movie.youTubeVideoKey,
                    imdbId = movie.imdbId,
                    originalLanguage = movie.originalLanguage,
                    spokenLanguages = movie.spokenLanguages,
                    originalTitle = movie.originalTitle,
                    displayTitle = movie.displayTitle,
                    metadata = movie.metadata,
                    runtime = movie.runtime,
                ),
            )
            val movieGenres = movie.genreIds.split(",")
                .filter { it.trim().isNotEmpty() }
                .map {
                    MovieGenreEntity(movieId = movie.id, genreId = it.trim().toLong(10))
                }
            movieDao.insertMovieGenres(movieGenres)
        }
    }
}
