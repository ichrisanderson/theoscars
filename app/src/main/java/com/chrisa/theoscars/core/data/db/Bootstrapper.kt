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

import com.chrisa.theoscars.core.data.db.category.CategoryHelper
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasHelper
import com.chrisa.theoscars.core.data.db.genre.GenreHelper
import com.chrisa.theoscars.core.data.db.movie.MovieHelper
import com.chrisa.theoscars.core.data.db.nomination.NominationHelper
import javax.inject.Inject

interface Bootstrapper {
    fun insertData()
}

class DefaultBootstrapper @Inject constructor(
    private val categoryAliasHelper: CategoryAliasHelper,
    private val categoryHelper: CategoryHelper,
    private val genreHelper: GenreHelper,
    private val nominationHelper: NominationHelper,
    private val movieHelper: MovieHelper,
) : Bootstrapper {
    override fun insertData() {
        categoryAliasHelper.insertData()
        categoryHelper.insertData()
        genreHelper.insertData()
        movieHelper.insertData()
        nominationHelper.insertData()
    }
}
