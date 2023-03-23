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

package com.chrisa.theoscars.core.data.db.nomination

import com.chrisa.theoscars.core.data.db.AppDatabase
import javax.inject.Inject

class NominationHelper @Inject constructor(
    private val appDatabase: AppDatabase,
    private val dataSource: NominationDataSource,
) {
    private val dao = appDatabase.nominationDao()

    fun insertData() {
        val items = dao.countAll()
        if (items > 0) return

        val dataSourceItems = dataSource.getNominations()
        val categoryKeys = appDatabase.categoryDao().allCategories().associate { it.id to it.name }
        val movieKeys = appDatabase.movieDao().allMovies().associate { it.id to it.title }

        dataSourceItems.forEach {
            if (!categoryKeys.containsKey(it.categoryId)) {
                throw IllegalStateException("Category not found $it. ${categoryKeys.size}")
            }

            if (!movieKeys.containsKey(it.movieId)) {
                throw IllegalStateException("Movie not found $it. ${movieKeys.size}")
            }

            dao.insert(
                NominationEntity(
                    year = it.ceremonyYear,
                    categoryId = it.categoryId,
                    movieId = it.movieId,
                    content = it.content,
                    winner = it.winner,
                ),
            )
        }
    }
}
