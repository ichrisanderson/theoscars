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

package com.chrisa.theoscars.core.data.db

import com.chrisa.theoscars.core.data.db.category.CategoryDao
import com.chrisa.theoscars.core.data.db.category.FakeCategoryDao
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasDao
import com.chrisa.theoscars.core.data.db.categoryalias.FakeCategoryAliasDao
import com.chrisa.theoscars.core.data.db.genre.FakeGenreDao
import com.chrisa.theoscars.core.data.db.genre.GenreDao
import com.chrisa.theoscars.core.data.db.movie.FakeMovieDao
import com.chrisa.theoscars.core.data.db.movie.MovieDao
import com.chrisa.theoscars.core.data.db.nomination.FakeNominationDao
import com.chrisa.theoscars.core.data.db.nomination.NominationDao

class FakeAppDatabase(
    private val categoryAliasDao: CategoryAliasDao = FakeCategoryAliasDao(),
    private val categoryDao: CategoryDao = FakeCategoryDao(),
    private val genreDao: GenreDao = FakeGenreDao(),
    private val movieDao: MovieDao = FakeMovieDao(),
    private val nominationDao: NominationDao = FakeNominationDao(categoryDao, movieDao),
) : AppDatabase {

    override fun nominationDao(): NominationDao = nominationDao
    override fun movieDao(): MovieDao = movieDao
    override fun categoryAliasDao(): CategoryAliasDao = categoryAliasDao
    override fun categoryDao(): CategoryDao = categoryDao
    override fun genreDao(): GenreDao = genreDao
}
