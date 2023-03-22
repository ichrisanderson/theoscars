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

package com.chrisa.theoscars.features.home.domain

import com.chrisa.theoscars.core.util.coroutines.CoroutineDispatchers
import com.chrisa.theoscars.features.home.data.HomeDataRepository
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadCategoriesUseCase @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val homeDataRepository: HomeDataRepository,
) {
    suspend fun execute(): List<CategoryModel> = withContext(coroutineDispatchers.io) {
        val categories = homeDataRepository.allCategories()
        val categoryAliasMap =
            homeDataRepository.allCategoryAliases().associate { it.id to it.name }
        val categoryMap = mutableMapOf<Long, MutableList<Long>>()

        categories.forEach {
            if (!categoryMap.containsKey(it.categoryAliasId)) {
                categoryMap[it.categoryAliasId] = mutableListOf()
            }
            categoryMap[it.categoryAliasId]!!.add(it.id)
        }

        val allCategoryModel = CategoryModel(name = "All", ids = categories.map { it.id })

        val result = mutableListOf<CategoryModel>()
        result.add(allCategoryModel)

        val mappedModels = categoryMap.map { e ->
            CategoryModel(name = categoryAliasMap[e.key]!!, ids = e.value)
        }
        result.addAll(mappedModels)

        return@withContext result
    }
}
