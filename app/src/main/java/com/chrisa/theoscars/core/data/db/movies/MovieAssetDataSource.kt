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

package com.chrisa.theoscars.core.data.db.movies

import com.chrisa.theoscars.core.data.db.AssetFileManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source
import java.io.InputStream
import javax.inject.Inject

class MovieAssetDataSource @Inject constructor(
    private val moshi: Moshi,
    private val assetFileManager: AssetFileManager,
) : MovieDataSource {

    override fun getMovies(): List<MovieData> {
        val type = Types.newParameterizedType(List::class.java, MovieData::class.java)
        val adapter = moshi.adapter<List<MovieData>>(type)
        return adapter.fromJson(assetFile().source().buffer())!!
    }

    private fun assetFile(): InputStream =
        assetFileManager.openFile("movies.json")
}
