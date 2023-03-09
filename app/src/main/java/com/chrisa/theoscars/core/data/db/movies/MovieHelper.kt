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

package com.chrisa.theoscars.core.data.db.movies

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.LocalDateConverter
import com.chrisa.theoscars.core.data.db.MovieEntity
import javax.inject.Inject

class MovieHelper @Inject constructor(
    appDatabase: AppDatabase,
    private val dataSource: MovieDataSource,
    private val localDateConverter: LocalDateConverter,
) {
    private val dao = appDatabase.movieDao()

    fun insertData() {
        val items = dao.countAll()
        if (items > 0) return

        val dataSourceItems = dataSource.getMovies()

        dataSourceItems.forEach { movie ->

            dao.insert(
                MovieEntity(
                    id = movie.id,
                    backdropImagePath = movie.backdropImagePath,
                    posterImagePath = movie.posterImagePath,
                    overview = movie.overview,
                    title = movie.title,
                    ceremonyYear = movie.nominationYear,
                    releaseDate = localDateConverter.fromTimestamp(movie.releaseDate)!!,
                    youTubeVideoKey = movie.youTubeVideoKey,
                    genreIds = movie.genreIds,
                ),
            )
        }
    }
}
