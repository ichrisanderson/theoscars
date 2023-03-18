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

package com.chrisa.theoscars.features.home.data

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.nomination.MovieSummary
import javax.inject.Inject

class HomeDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {

    fun allMoviesForCeremony(year: Int): List<MovieSummary> {
        val dao = appDatabase.nominationDao()
        return dao.allMoviesForCeremony(year)
    }

    fun allMoviesForCeremonyWithFilter(categories: List<Long>, year: Int): List<MovieSummary> {
        val dao = appDatabase.nominationDao()
        return dao.allMoviesForCeremonyWithFilter(categories, year)
    }
}
