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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chrisa.theoscars.core.data.db.category.CategoryDao
import com.chrisa.theoscars.core.data.db.category.CategoryEntity
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasDao
import com.chrisa.theoscars.core.data.db.categoryalias.CategoryAliasEntity
import com.chrisa.theoscars.core.data.db.genre.GenreDao
import com.chrisa.theoscars.core.data.db.genre.GenreEntity
import com.chrisa.theoscars.core.data.db.movie.MovieDao
import com.chrisa.theoscars.core.data.db.movie.MovieEntity
import com.chrisa.theoscars.core.data.db.movie.MovieGenreEntity
import com.chrisa.theoscars.core.data.db.nomination.NominationDao
import com.chrisa.theoscars.core.data.db.nomination.NominationEntity
import com.chrisa.theoscars.core.data.db.watchlist.WatchlistDao
import com.chrisa.theoscars.core.data.db.watchlist.WatchlistEntity

interface AppDatabase {
    fun nominationDao(): NominationDao
    fun movieDao(): MovieDao
    fun categoryAliasDao(): CategoryAliasDao
    fun categoryDao(): CategoryDao
    fun genreDao(): GenreDao
    fun watchlistDao(): WatchlistDao

    fun beginTransaction()
    fun setTransactionSuccessful()
    fun endTransaction()
}

@Database(
    entities = [
        CategoryAliasEntity::class,
        CategoryEntity::class,
        GenreEntity::class,
        NominationEntity::class,
        MovieEntity::class,
        MovieGenreEntity::class,
        WatchlistEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AndroidAppDatabase : RoomDatabase(), AppDatabase {

    abstract override fun nominationDao(): NominationDao
    abstract override fun movieDao(): MovieDao
    abstract override fun categoryAliasDao(): CategoryAliasDao
    abstract override fun categoryDao(): CategoryDao
    abstract override fun genreDao(): GenreDao
    abstract override fun watchlistDao(): WatchlistDao

    companion object {
        private const val databaseName = "the-oscars-db"

        fun buildDatabase(context: Context): AppDatabase {
            val seedDBExists = context.assets.list("")?.contains(databaseName) ?: false
            return Room.databaseBuilder(context, AndroidAppDatabase::class.java, databaseName)
                .apply {
                    if (seedDBExists) {
                        createFromAsset(databaseName)
                    }
                }
                .build()
        }
    }
}
