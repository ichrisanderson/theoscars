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

import com.chrisa.theoscars.core.data.db.category.CategorySeedData
import com.chrisa.theoscars.core.data.db.genre.GenreSeedData
import com.chrisa.theoscars.core.data.db.movie.MovieSeedData
import com.chrisa.theoscars.core.data.db.nomination.NominationSeedData
import java.io.InputStream

class FakeAssetFileManager : AssetFileManager {
    override fun openFile(fileName: String): InputStream {
        return when (fileName) {
            "movies.json" -> MovieSeedData.movies2022.byteInputStream()
            "nominations.json" -> NominationSeedData.nominations2022.byteInputStream()
            "categories.json" -> CategorySeedData.categories2022.byteInputStream()
            "genres.json" -> GenreSeedData.genres.byteInputStream()
            else -> "".byteInputStream()
        }
    }
}
