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

package com.chrisa.theoscars.features.search.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.search.domain.models.SearchResultModel
import timber.log.Timber

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onMovieClick: (Long) -> Unit,
    onClose: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    SearchScreenContent(
        searchQuery = viewState.searchQuery,
        searchResults = viewState.searchResults,
        onSearchTextChanged = viewModel::updateQuery,
        onClearClick = viewModel::clearQuery,
        onMovieClick = onMovieClick,
        onClose = onClose,
        modifier = Modifier.testTag("searchScreenContent"),
    )
}

@Composable
private fun SearchScreenContent(
    searchQuery: String,
    searchResults: List<SearchResultModel>,
    onMovieClick: (Long) -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.tag("UI_TEST").d("searchQuery=$searchQuery;searchResults=${searchResults.size}")
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
        topBar = {
            SearchBar(
                searchText = searchQuery,
                onSearchTextChanged = onSearchTextChanged,
                placeholderText = stringResource(id = R.string.search_placeholder),
                onClearClick = onClearClick,
                onNavigateBack = onClose,
            )
        },
    ) { padding ->
        if (searchResults.isEmpty()) {
            EmptySearchResults(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(items = searchResults, key = { it.movieId }) { model ->
                    SearchResultCard(
                        searchResultModel = model,
                        onClick = onMovieClick,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.empty_search_results_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.empty_search_results_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
fun SearchResultCard(
    searchResultModel: SearchResultModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .clickable { onClick(searchResultModel.movieId) }
            .testTag("movieCard_${searchResultModel.movieId}"),
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
            if (searchResultModel.posterImagePath == null) {
                Image(
                    painter = painterResource(R.drawable.show_reel),
                    colorFilter = ColorFilter.tint(Color.LightGray),
                    contentDescription = stringResource(
                        id = R.string.movie_image_default_format,
                        searchResultModel.title,
                    ),
                    modifier = Modifier
                        .width(96.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .aspectRatio(3 / 4.0f)
                        .padding(16.dp),
                )
            } else {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500/${searchResultModel.posterImagePath}",
                    contentDescription = stringResource(
                        id = R.string.movie_image_default_format,
                        searchResultModel.title,
                    ),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(96.dp)
                        .aspectRatio(3 / 4.0f),
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    text = searchResultModel.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = searchResultModel.year.prefixWithCeremonyEmoji(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    placeholderText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        windowInsets = WindowInsets(top = 0.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .onFocusChanged { focusState ->
                        showClearButton = (focusState.isFocused)
                    }
                    .testTag("searchBar")
                    .focusRequester(focusRequester),
                value = searchText,
                onValueChange = onSearchTextChanged,
                placeholder = {
                    Text(
                        text = placeholderText,
                        modifier = Modifier
                            .testTag("searchBarPlaceholderText"),
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = showClearButton,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(
                            onClick = { onClearClick() },
                            modifier = Modifier.testTag("clearSearchButton"),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(id = R.string.clear_query_button_description),
                            )
                        }
                    }
                },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier.testTag("closeButton"),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    modifier = Modifier,
                    contentDescription = stringResource(id = R.string.close_button_description),
                )
            }
        },
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    OscarsTheme {
        Surface {
            SearchScreenContent(
                searchQuery = "",
                searchResults = listOf(
                    SearchResultModel(
                        movieId = 1,
                        posterImagePath = null,
                        title = "All Quiet on the Western Front",
                        year = "2022",
                    ),
                    SearchResultModel(
                        movieId = 2,
                        posterImagePath = null,
                        title = "Everything Everywhere All At Once",
                        year = "2022",
                    ),
                    SearchResultModel(
                        movieId = 3,
                        posterImagePath = null,
                        title = "Avatar: The Way of Water",
                        year = "2022",
                    ),
                ),
                onSearchTextChanged = { },
                onMovieClick = { },
                onClearClick = { },
                onClose = {},
            )
        }
    }
}

@Preview
@Composable
fun SearchEmptyScreenPreview() {
    OscarsTheme {
        Surface {
            SearchScreenContent(
                searchQuery = "",
                searchResults = emptyList(),
                onSearchTextChanged = { },
                onMovieClick = { },
                onClearClick = { },
                onClose = {},
            )
        }
    }
}

@Preview
@Composable
fun SearchResultCardPreview() {
    OscarsTheme {
        Surface {
            SearchResultCard(
                searchResultModel = SearchResultModel(
                    movieId = 1234,
                    posterImagePath = null,
                    title = "All Quiet on the Western Front",
                    year = "2023",
                ),
                onClick = { },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
