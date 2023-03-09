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
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

interface AppDatabase {

    fun nominationDao(): NominationDao
    fun movieDao(): MovieDao
}

@Database(
    entities = [
        NominationEntity::class,
        MovieEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
)
abstract class AndroidAppDatabase : RoomDatabase(), AppDatabase {

    abstract override fun nominationDao(): NominationDao
    abstract override fun movieDao(): MovieDao

    companion object {
        private const val databaseName = "the-oscars-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AndroidAppDatabase::class.java, databaseName)
//                .createFromAsset("the-oscars-db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

class LocalDateConverter @Inject constructor() {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }
}

@Dao
interface NominationDao {

    @Query("SELECT COUNT(nominationId) FROM nomination")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: NominationEntity)

    @Query("SELECT DISTINCT category FROM nomination WHERE ceremonyYear = :ceremonyYear")
    fun allCategoriesForCeremony(ceremonyYear: Int): List<String>

    @Query("SELECT DISTINCT * FROM nomination WHERE film = :film AND ceremonyYear = :ceremonyYear")
    fun allCeremonyNominationsWithMovieTitle(film: String, ceremonyYear: Int): List<NominationEntity>

    @Query("SELECT DISTINCT * FROM nomination WHERE ceremonyYear = :ceremonyYear")
    fun allNominationsForCeremony(ceremonyYear: Int): List<NominationEntity>

    @Query("SELECT DISTINCT name FROM nomination WHERE ceremonyYear = :ceremonyYear")
    fun allNamesForCeremony(ceremonyYear: Int): List<String>
}

@Dao
interface MovieDao {

    @Query("SELECT COUNT(id) FROM movie")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: MovieEntity)

    @Query("SELECT * FROM movie WHERE title = :title AND ceremonyYear = :ceremonyYear")
    fun findMoviesForCeremony(title: String, ceremonyYear: Int): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE id = :id LIMIT 1")
    fun loadMovie(id: Long): MovieEntity?

    @Query("SELECT * FROM movie WHERE ceremonyYear = :ceremonyYear ")
    fun allMoviesForCeremony(ceremonyYear: Int): List<MovieEntity>

    @Query("SELECT * FROM movie")
    fun allMovies(): List<MovieEntity>
}

@Entity(
    tableName = "nomination",
)
data class NominationEntity(
    @PrimaryKey(autoGenerate = true)
    var nominationId: Long = 0,
    val ceremony: Int,
    val ceremonyYear: Int,
    val category: String,
    val film: String,
    val filmYear: Int,
    val name: String,
    val winner: Boolean?,
)

@Entity(
    tableName = "movie",
)
data class MovieEntity(
    @PrimaryKey
    val id: Long,
    val backdropImagePath: String?,
    val posterImagePath: String?,
    val overview: String,
    val title: String,
    val ceremonyYear: Int,
    val releaseDate: LocalDate?,
    val youTubeVideoKey: String?,
    val genreIds: String,
)
