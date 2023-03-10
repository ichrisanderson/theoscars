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

package com.chrisa.theoscars.features.movie.data

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.MovieEntity
import com.chrisa.theoscars.core.data.db.NominationEntity
import javax.inject.Inject

class MovieDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {

    fun loadMovie(id: Long): MovieEntity? {
        val dao = appDatabase.movieDao()
        return dao.loadMovie(id)
    }

    fun loadNominations(title: String, ceremonyYear: Int): List<NominationEntity> {
        val dao = appDatabase.nominationDao()
        return dao.allCeremonyNominationsWithMovieTitle(title, ceremonyYear)
    }
}
