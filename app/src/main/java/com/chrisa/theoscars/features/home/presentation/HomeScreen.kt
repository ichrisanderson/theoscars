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

package com.chrisa.theoscars.features.home.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val transition = updateTransition(viewState, label = "splashTransition")
    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "contentAlpha",
    ) { vs ->
        if (vs.isLoading) 0f else 1f
    }
    val contentTopPadding by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "contentTopPadding",
    ) { vs ->
        if (vs.isLoading) 100.dp else 0.dp
    }
    HomeContent(
        movies = viewState.movies,
        onMovieClick = onMovieClick,
        onSearchClick = onSearchClick,
        onFilterClick = {
            coroutineScope.launch {
                openBottomSheet = true
            }
        },
        modifier = Modifier.alpha(contentAlpha),
        topPadding = contentTopPadding,
    )
    if (openBottomSheet) {
        BackHandler {
            openBottomSheet = false
        }
        FilterSheet(
            categories = viewState.categories,
            selectedCategories = viewState.selectedCategories,
            onDismissRequest = {
                openBottomSheet = false
            },
        ) {
            openBottomSheet = false
            viewModel.setSelectedCategories(it)
        }
    }
}

@Composable
private fun HomeContent(
    movies: List<MovieSummaryModel>,
    onMovieClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.padding(top = topPadding))
        AppBar(
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick,
        )
        if (movies.isEmpty()) {
            EmptyMovies()
        } else {
            LazyColumn {
                items(items = movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMovies(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.empty_movies_results_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.empty_movies_results_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(top = 0.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_oscar),
                contentDescription = "",
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}

@Composable
fun MovieCard(
    movie: MovieSummaryModel,
    onMovieClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .padding(16.dp)
            .clickable { onMovieClick(movie.id) },
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            if (movie.backdropImagePath == null) {
                Image(
                    painter = painterResource(R.drawable.show_reel),
                    colorFilter = ColorFilter.tint(Color.LightGray),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .padding(32.dp)
                        .aspectRatio(16 / 9.0f),
                )
            } else {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500/${movie.backdropImagePath}",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9.0f),
                )
            }
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            )
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    categories: List<CategoryModel>,
    selectedCategories: List<CategoryModel>,
    onDismissRequest: () -> Unit,
    onApplySelection: (List<CategoryModel>) -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
    )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
    ) {
        FilterContent(
            categories,
            selectedCategories,
            onApplySelection,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    categories: List<CategoryModel>,
    selectedCategories: List<CategoryModel>,
    onApplySelection: (List<CategoryModel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategoriesStateList = remember { selectedCategories.toMutableStateList() }

    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.filter_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    id = R.string.categories_selected_title_format,
                    selectedCategoriesStateList.size,
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(vertical = 8.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                ),
                onClick = {
                    selectedCategoriesStateList.clear()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.clear_filter_cta),
                )
            }
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                ),
                onClick = {
                    selectedCategoriesStateList.clear()
                    selectedCategoriesStateList.addAll(categories)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(id = R.string.select_all_filter_cta),
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(0.3f),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            categories.forEach { category ->
                item {
                    FilterChip(
                        selected = selectedCategoriesStateList.contains(category),
                        onClick = {
                            if (selectedCategoriesStateList.contains(category)) {
                                selectedCategoriesStateList.remove(category)
                            } else {
                                selectedCategoriesStateList.add(category)
                            }
                        },
                        label = {
                            Text(
                                text = category.name,
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
        Button(
            onClick = { onApplySelection(selectedCategoriesStateList) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(128.dp)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.apply_filter_cta),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Preview
@Composable
fun HomeContentPreview() {
    OscarsTheme {
        Surface {
            HomeContent(
                movies = listOf(
                    MovieSummaryModel(
                        id = 49046,
                        backdropImagePath = null,
                        overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
                        title = "All Quiet on the Western Front",
                    ),
                    MovieSummaryModel(
                        id = 1043141,
                        backdropImagePath = null,
                        overview = "A cold night in December. Ebba waits for the tram to go home after a party, but the ride takes an unexpected turn.",
                        title = "Night Ride",
                    ),
                ),
                onMovieClick = { },
                onSearchClick = { },
                onFilterClick = { },
            )
        }
    }
}

@Preview
@Composable
fun HomeContentEmptyMoviesPreview() {
    OscarsTheme {
        Surface {
            HomeContent(
                movies = emptyList(),
                onFilterClick = { },
                onSearchClick = { },
                onMovieClick = { },
            )
        }
    }
}

@Preview
@Composable
fun MovieCardPreview() {
    OscarsTheme {
        Surface {
            MovieCard(
                movie = MovieSummaryModel(
                    id = 1234,
                    backdropImagePath = null,
                    overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army.",
                    title = "All Quiet on the Western Front",
                ),
                onMovieClick = { },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Preview
@Composable
fun FilterDialogPreview() {
    OscarsTheme {
        Surface {
            val categories = listOf(
                CategoryModel(name = "Best Picture", ids = listOf(48, 67, 127, 43, 8, 16)),
                CategoryModel(name = "International Feature Film", ids = listOf(9, 56, 111, 123)),
                CategoryModel(name = "Animated Feature Film", ids = listOf(106, 116)),
            )
            FilterContent(
                categories = categories,
                selectedCategories = categories.drop(1),
                onApplySelection = { },
            )
        }
    }
}
