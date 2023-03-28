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

package com.chrisa.theoscars.core.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.home.domain.models.GenreModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterList(
    @StringRes titleStringResId: Int,
    displayItems: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabelMapper: (T) -> String,
    modifier: Modifier = Modifier,
    testTagPostFix: String = "",
) {
    val listState = rememberLazyListState()

    LaunchedEffect(displayItems) {
        val selectedIndex = displayItems.indexOf(selectedItem)
        if (selectedIndex >= 0) {
            listState.scrollToItem(selectedIndex)
        }
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = titleStringResId),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(vertical = 8.dp),
            )
        }
        Divider(
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(0.3f),
        )
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("filterList_$testTagPostFix"),
        ) {
            displayItems.forEach { displayItem ->
                item(key = itemLabelMapper(displayItem)) {
                    FilterChip(
                        selected = selectedItem == displayItem,
                        onClick = { onItemSelected(displayItem) },
                        label = {
                            Text(
                                text = itemLabelMapper(displayItem),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .alpha(0.3f),
        )
    }
}

@Preview
@Composable
fun FilterListPreview() {
    OscarsTheme {
        Surface {
            val genres = listOf(
                GenreModel(name = "Action", id = 1L),
                GenreModel(name = "Comedy", id = 1L),
                GenreModel(name = "Drama", id = 1L),
            )
            FilterList(
                titleStringResId = R.string.genres_filter_title,
                displayItems = genres,
                selectedItem = genres[1],
                onItemSelected = { },
                itemLabelMapper = GenreModel::name,
            )
        }
    }
}
