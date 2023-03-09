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

package com.chrisa.theoscars.core.data.db.movies

import com.chrisa.theoscars.core.data.db.MovieDao
import com.chrisa.theoscars.core.data.db.MovieEntity

class FakeMovieDao : MovieDao {

    private var movies = mutableListOf<MovieEntity>()

    override fun countAll(): Int = movies.size

    override fun insert(item: MovieEntity) {
        movies.add(item)
    }

    override fun findMoviesForCeremony(title: String, ceremonyYear: Int): List<MovieEntity> {
        return movies.filter { it.title == title && it.ceremonyYear == ceremonyYear }
    }

    override fun loadMovie(id: Long): MovieEntity? {
        return movies.firstOrNull { it.id == id }
    }

    override fun allMoviesForCeremony(ceremonyYear: Int): List<MovieEntity> {
        return movies.filter { it.ceremonyYear == ceremonyYear }
    }

    override fun allMovies(): List<MovieEntity> {
        return movies
    }
}
