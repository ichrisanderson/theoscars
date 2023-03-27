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

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.core.util.YearValidator
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import com.chrisa.theoscars.features.home.domain.models.GenreModel
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
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
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            if (modalSheetState.isVisible) {
                val filterModel = viewState.filterModel
                Surface {
                    FilterContent(
                        categories = filterModel.categories,
                        selectedCategory = filterModel.selectedCategory,
                        genres = filterModel.genres,
                        selectedGenre = filterModel.selectedGenre,
                        currentStartYear = filterModel.startYearString,
                        currentEndYear = filterModel.endYearString,
                        winnersOnly = filterModel.winnersOnly,
                        onApplySelection = { newFilter ->
                            viewModel.updateFilter(newFilter)
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        },
                    )
                }
            }
        },
    ) {
        if (viewState.isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        } else {
            HomeContent(
                listState = lazyListState,
                movies = viewState.movies,
                onMovieClick = onMovieClick,
                onSearchClick = onSearchClick,
                onFilterClick = {
                    coroutineScope.launch {
                        modalSheetState.show()
                    }
                },
                modifier = Modifier.alpha(contentAlpha),
                topPadding = contentTopPadding,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    listState: LazyListState,
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
            LazyColumn(
                state = listState,
                modifier = Modifier.testTag("movieList"),
            ) {
                items(items = movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                        modifier = Modifier
                            .testTag("movieCard_${movie.id}")
                            .animateItemPlacement(),
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
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("searchButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.testTag("filterButton"),
            ) {
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
                text = movie.year.prefixWithCeremonyEmoji(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            )
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            )
        }
    }
}

@Composable
fun FilterContent(
    categories: List<CategoryModel>,
    selectedCategory: CategoryModel,
    genres: List<GenreModel>,
    selectedGenre: GenreModel,
    currentStartYear: String,
    currentEndYear: String,
    winnersOnly: Boolean,
    onApplySelection: (FilterModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var startYear by remember { mutableStateOf(currentStartYear) }
    var endYear by remember { mutableStateOf(currentEndYear) }
    val isStartYearError by remember { derivedStateOf { !YearValidator.isValidYear(startYear) } }
    val isEndYearError by remember { derivedStateOf { !YearValidator.isValidYear(endYear) } }
    var selectedCategoryState by remember { mutableStateOf(selectedCategory) }
    var selectedGenreState by remember { mutableStateOf(selectedGenre) }
    var winnersOnlyState by remember { mutableStateOf(winnersOnly) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.filter_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .testTag("filterContentList")
                .verticalScroll(scrollState),
        ) {
            YearFilter(
                startYear = startYear,
                isStartYearError = isStartYearError,
                endYear = endYear,
                isEndYearError = isEndYearError,
                onStartYearChanged = {
                    startYear = it
                },
                onEndYearChanged = {
                    endYear = it
                },
            )
            ItemRowFilter(
                displayItems = categories,
                selectedItem = selectedCategoryState,
                onItemSelected = { selectedCategoryState = it },
                titleFormatId = R.string.categories_filter_title,
                nameLabelMapper = CategoryModel::name,
                modifier = Modifier
                    .padding(top = 8.dp),
                testTagPostFix = "Categories",
            )
            ItemRowFilter(
                selectedItem = selectedGenreState,
                onItemSelected = { selectedGenreState = it },
                displayItems = genres,
                titleFormatId = R.string.genres_filter_title,
                nameLabelMapper = GenreModel::name,
                modifier = Modifier
                    .padding(top = 16.dp),
                testTagPostFix = "Genres",
            )
            WinnersFilter(
                isSelected = winnersOnlyState,
                onSelectionChange = {
                    winnersOnlyState = it
                },
                modifier = Modifier
                    .padding(top = 16.dp),
            )
            Button(
                enabled = !isStartYearError && !isEndYearError,
                onClick = {
                    onApplySelection(
                        FilterModel(
                            startYear = startYear.toInt(10),
                            endYear = endYear.toInt(10),
                            categories = categories,
                            selectedCategory = selectedCategoryState,
                            genres = genres,
                            selectedGenre = selectedGenreState,
                            winnersOnly = winnersOnlyState,
                        ),
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(128.dp)
                    .padding(vertical = 16.dp)
                    .testTag("applyButton"),
            ) {
                Text(
                    text = stringResource(id = R.string.apply_filter_cta),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun YearFilter(
    startYear: String,
    isStartYearError: Boolean,
    endYear: String,
    isEndYearError: Boolean,
    onStartYearChanged: (String) -> Unit,
    onEndYearChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.year_filter_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        )
        Divider(
            modifier = Modifier
                .alpha(0.3f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.from_label),
                modifier = Modifier.padding(start = 16.dp),
            )
            OutlinedTextField(
                value = startYear,
                onValueChange = onStartYearChanged,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isStartYearError,
                modifier = Modifier
                    .width(128.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("startYear"),
            )
            Text(
                text = stringResource(id = R.string.to_label),
            )
            OutlinedTextField(
                value = endYear,
                onValueChange = onEndYearChanged,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isEndYearError,
                modifier = Modifier
                    .width(128.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("endYear"),
            )
        }
        if (isStartYearError || isEndYearError) {
            Text(
                text = stringResource(id = R.string.year_filter_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 8.dp),
            )
        }
        Divider(
            modifier = Modifier
                .alpha(0.3f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ItemRowFilter(
    displayItems: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    @StringRes titleFormatId: Int,
    nameLabelMapper: (T) -> String,
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
                text = stringResource(id = titleFormatId),
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
                .testTag("itemRowFilter_$testTagPostFix"),
        ) {
            displayItems.forEach { displayItem ->
                item(key = nameLabelMapper(displayItem)) {
                    FilterChip(
                        selected = selectedItem == displayItem,
                        onClick = { onItemSelected(displayItem) },
                        label = {
                            Text(
                                text = nameLabelMapper(displayItem),
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

@Composable
private fun WinnersFilter(
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("winnersOnlyRow")
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Switch,
                onClick = {
                    onSelectionChange(!isSelected)
                },
            ),
    ) {
        Text(
            text = stringResource(id = R.string.winners_filter_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(vertical = 8.dp),
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Switch(
            checked = isSelected,
            onCheckedChange = onSelectionChange,
            modifier = Modifier
                .testTag("winnersOnlySwitch")
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
fun HomeContentPreview() {
    OscarsTheme {
        Surface {
            HomeContent(
                listState = rememberLazyListState(),
                movies = listOf(
                    MovieSummaryModel(
                        id = 49046,
                        backdropImagePath = null,
                        overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
                        title = "All Quiet on the Western Front",
                        year = "2023",
                    ),
                    MovieSummaryModel(
                        id = 1043141,
                        backdropImagePath = null,
                        overview = "A cold night in December. Ebba waits for the tram to go home after a party, but the ride takes an unexpected turn.",
                        title = "Night Ride",
                        year = "2023",
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
                listState = rememberLazyListState(),
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
                    year = "2023",
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
                CategoryModel(name = "Best Picture", id = 48),
                CategoryModel(name = "International Feature Film", id = 9),
                CategoryModel(name = "Animated Feature Film", id = 106),
            )
            val genres = listOf(
                GenreModel(name = "Action", id = 1L),
                GenreModel(name = "Comedy", id = 1L),
                GenreModel(name = "Drama", id = 1L),
            )
            FilterContent(
                categories = categories,
                selectedCategory = categories.first(),
                genres = genres,
                selectedGenre = genres.last(),
                currentStartYear = "2023",
                currentEndYear = "2023",
                winnersOnly = false,
                onApplySelection = { },
            )
        }
    }
}
