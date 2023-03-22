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

import android.content.Context
import android.content.res.AssetManager
import com.chrisa.theoscars.core.data.db.category.CategoryAssetDataSource
import com.chrisa.theoscars.core.data.db.category.CategoryDataSource
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasAssetDataSource
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasDataSource
import com.chrisa.theoscars.core.data.db.genre.GenreAssetDataSource
import com.chrisa.theoscars.core.data.db.genre.GenreDataSource
import com.chrisa.theoscars.core.data.db.movie.MovieAssetDataSource
import com.chrisa.theoscars.core.data.db.movie.MovieDataSource
import com.chrisa.theoscars.core.data.db.nomination.NominationAssetDataSource
import com.chrisa.theoscars.core.data.db.nomination.NominationDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AndroidAppDatabase.buildDatabase(context)

    @Provides
    fun provideAssetFileManager(assetManager: AssetManager): AssetFileManager =
        AndroidAssetFileManager(assetManager)

    @Provides
    fun provideBootstrapper(bootstrapper: DefaultBootstrapper): Bootstrapper = bootstrapper

    @Provides
    fun provideMovieDataSource(movieAssetDataSource: MovieAssetDataSource): MovieDataSource =
        movieAssetDataSource

    @Provides
    fun provideNominationDataSource(nominationAssetDataSource: NominationAssetDataSource): NominationDataSource =
        nominationAssetDataSource

    @Provides
    fun provideCategoryAliasAssetDataSource(categoryAliasAssetDataSource: CategoryAliasAssetDataSource): CategoryAliasDataSource =
        categoryAliasAssetDataSource

    @Provides
    fun provideCategoryAssetDataSource(categoryAssetDataSource: CategoryAssetDataSource): CategoryDataSource =
        categoryAssetDataSource

    @Provides
    fun provideGenreAssetDataSource(genreAssetDataSource: GenreAssetDataSource): GenreDataSource =
        genreAssetDataSource
}
