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

package com.chrisa.theoscars.core.data.db.nominations

import com.chrisa.theoscars.core.data.db.AppDatabase
import com.chrisa.theoscars.core.data.db.NominationEntity
import javax.inject.Inject

class NominationHelper @Inject constructor(
    appDatabase: AppDatabase,
    private val dataSource: NominationDataSource,
) {
    private val dao = appDatabase.nominationDao()

    fun insertData() {
        val items = dao.countAll()
        if (items > 0) return

        val dataSourceItems = dataSource.getNominations()

        dataSourceItems.forEach {
            dao.insert(
                NominationEntity(
                    ceremony = it.ceremony,
                    ceremonyYear = it.ceremonyYear,
                    category = it.category,
                    film = it.film,
                    filmYear = it.filmYear,
                    name = it.name,
                    winner = it.winner,
                ),
            )
        }
    }
}
