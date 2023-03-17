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

import com.chrisa.theoscars.core.data.LocalDateJsonAdapter
import com.chrisa.theoscars.core.data.LocalDateTimeJsonAdapter
import com.chrisa.theoscars.core.data.db.category.CategoryAssetDataSource
import com.chrisa.theoscars.core.data.db.category.CategoryHelper
import com.chrisa.theoscars.core.data.db.genre.GenreAssetDataSource
import com.chrisa.theoscars.core.data.db.genre.GenreHelper
import com.chrisa.theoscars.core.data.db.movie.MovieAssetDataSource
import com.chrisa.theoscars.core.data.db.movie.MovieHelper
import com.chrisa.theoscars.core.data.db.nomination.NominationAssetDataSource
import com.chrisa.theoscars.core.data.db.nomination.NominationHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class BootstrapperBuilder {

    fun build(appDatabase: AppDatabase, assetFileManager: AssetFileManager): Bootstrapper {
        val moshi = Moshi.Builder()
            .add(LocalDateJsonAdapter())
            .add(LocalDateTimeJsonAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return DefaultBootstrapper(
            categoryHelper = CategoryHelper(
                appDatabase,
                CategoryAssetDataSource(moshi, assetFileManager),
            ),
            genreHelper = GenreHelper(
                appDatabase,
                GenreAssetDataSource(moshi, assetFileManager),
            ),
            nominationHelper = NominationHelper(
                appDatabase,
                NominationAssetDataSource(moshi, assetFileManager),
            ),
            movieHelper = MovieHelper(
                appDatabase,
                MovieAssetDataSource(moshi, assetFileManager),
                LocalDateConverter(),
            ),
        )
    }
}
