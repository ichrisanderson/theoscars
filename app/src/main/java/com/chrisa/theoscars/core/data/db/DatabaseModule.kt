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
import com.chrisa.theoscars.core.data.db.movies.MovieAssetDataSource
import com.chrisa.theoscars.core.data.db.movies.MovieDataSource
import com.chrisa.theoscars.core.data.db.nominations.NominationAssetDataSource
import com.chrisa.theoscars.core.data.db.nominations.NominationDataSource
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
}
