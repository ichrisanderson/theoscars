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

package com.chrisa.theoscars.core.data.db.categoryalias

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryAliasDao {

    @Query("SELECT COUNT(id) FROM categoryAlias")
    fun countAll(): Int

    @Query("SELECT * FROM categoryAlias ORDER BY name")
    fun allCategoryAliases(): List<CategoryAliasEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryAlias: CategoryAliasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categoryAliases: List<CategoryAliasEntity>)
}
