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

import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source
import java.io.InputStream

interface AssetFileManager {
    fun openFile(fileName: String): InputStream

    companion object {
        fun <T> AssetFileManager.openFileAsList(
            fileName: String,
            moshi: Moshi,
            itemType: Class<T>,
        ): List<T> {
            val type = Types.newParameterizedType(List::class.java, itemType)
            val adapter = moshi.adapter<List<T>>(type)
            return adapter.fromJson(openFile(fileName).source().buffer())!!
        }
    }
}

class AndroidAssetFileManager(private val assetManager: AssetManager) : AssetFileManager {
    override fun openFile(fileName: String): InputStream = assetManager.open(fileName)
}
