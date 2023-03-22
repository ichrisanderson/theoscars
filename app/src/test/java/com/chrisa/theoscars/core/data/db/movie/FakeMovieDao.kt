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

class FakeMovieDao : MovieDao {

    private var movies = mutableListOf<MovieEntity>()
    private var moviesGenres = mutableListOf<MovieGenreEntity>()

    override fun countAll(): Int = movies.size

    override fun allMovies(): List<MovieEntity> = movies

    override fun allMovieGenres(): List<MovieGenreEntity> = moviesGenres

    override fun insert(item: MovieEntity) {
        movies.add(item)
    }

    override fun loadMovie(id: Long): MovieEntity? {
        return movies.firstOrNull { it.id == id }
    }

    override fun searchMovies(query: String): List<MovieEntity> {
        val queryText = query.substring(1, query.lastIndex)
        val regex = Regex("^.*?$queryText.*?\$", RegexOption.IGNORE_CASE)
        return movies.filter { it.title.matches(regex) }
    }

    override fun insertMovieGenres(items: List<MovieGenreEntity>) {
        moviesGenres.addAll(items)
    }
}
