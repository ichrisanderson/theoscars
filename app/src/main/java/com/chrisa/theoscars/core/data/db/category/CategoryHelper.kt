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

package com.chrisa.theoscars.core.data.db.category

import com.chrisa.theoscars.core.data.db.AppDatabase
import javax.inject.Inject

class CategoryHelper @Inject constructor(
    private val appDatabase: AppDatabase,
    private val dataSource: CategoryDataSource,
) {
    private val dao = appDatabase.categoryDao()

    fun insertData() {
        val items = dao.countAll()
        if (items > 0) return

        val entities = dataSource.getCategories()
            .map {
                CategoryEntity(id = it.id, categoryAliasId = it.aliasId, name = it.name)
            }

        dao.insertAll(entities)
    }
}
