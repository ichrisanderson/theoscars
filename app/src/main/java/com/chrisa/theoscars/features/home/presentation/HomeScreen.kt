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

import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.FilterList
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.core.util.YearValidator
import com.chrisa.theoscars.features.home.domain.models.CategoryModel
import com.chrisa.theoscars.features.home.domain.models.GenreModel
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel
import com.chrisa.theoscars.features.home.domain.models.SortDirection
import com.chrisa.theoscars.features.home.domain.models.SortOrder

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onMovieClick: (Long) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val lazyListState = rememberLazyListState()

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
            filter = viewState.filterModel,
            movies = viewState.movies,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
            onMovieClick = onMovieClick,
            onWatchedClick = viewModel::setWatchedStatus,
            onWatchlistClick = viewModel::toggleWatchlistStatus,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    listState: LazyListState,
    filter: FilterModel,
    movies: List<MovieSummaryModel>,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onMovieClick: (Long) -> Unit,
    onWatchedClick: (Long?, Long, Boolean) -> Unit,
    onWatchlistClick: (Long?, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (movies.isEmpty()) {
            EmptyMovies(
                filter = filter,
                onFilterClick = onFilterClick,
                onSortClick = onSortClick,
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.testTag("movieList"),
            ) {
                stickyHeader {
                    FilterBar(
                        filter = filter,
                        onFilterClick = onFilterClick,
                        onSortClick = onSortClick,
                    )
                }
                items(items = movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                        onWatchedClick = onWatchedClick,
                        onWatchlistClick = onWatchlistClick,
                        modifier = Modifier
                            .testTag("movieCard_${movie.id}")
                            .animateItemPlacement(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(
    filter: FilterModel,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.66f)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier.testTag("filterButton"),
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = stringResource(id = R.string.filter_icon_description),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        val resources = LocalContext.current.resources

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f),
        ) {
            for (selectedFilterItem in selectedFilterItems(resources, filter)) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = onFilterClick,
                        label = {
                            Text(
                                text = selectedFilterItem,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                    )
                }
            }
        }
        IconButton(
            onClick = onSortClick,
            modifier = Modifier.testTag("sortButton"),
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = stringResource(id = R.string.sort_icon_description),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun EmptyMovies(
    filter: FilterModel,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        FilterBar(
            filter = filter,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
        )
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
}

private fun selectedFilterItems(resources: Resources, filter: FilterModel): List<String> {
    val items = mutableListOf<String>()
    if (filter.startYear == filter.endYear) {
        items.add(filter.startYearString)
    } else {
        items.add(filter.startYearString + " - " + filter.endYearString)
    }
    if (filter.selectedCategory.id > 0L) {
        items.add(filter.selectedCategory.name)
    }
    if (filter.selectedGenre.id > 0L) {
        items.add(filter.selectedGenre.name)
    }
    if (filter.winnersOnly) {
        items.add(resources.getString(R.string.winners_label))
    }
    return items
}

@Composable
fun MovieCard(
    movie: MovieSummaryModel,
    onMovieClick: (Long) -> Unit,
    onWatchedClick: (Long?, Long, Boolean) -> Unit,
    onWatchlistClick: (Long?, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradientBrush = remember {
        Brush.verticalGradient(colors = listOf(Color.Black, Color.Transparent))
    }

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .padding(16.dp)
            .clickable { onMovieClick(movie.id) },
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Box {
                if (movie.backdropImagePath == null) {
                    Image(
                        painter = painterResource(R.drawable.show_reel),
                        colorFilter = ColorFilter.tint(Color.LightGray),
                        contentDescription = stringResource(
                            id = R.string.movie_image_default_format,
                            movie.title,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(32.dp)
                            .aspectRatio(16 / 9.0f),
                    )
                } else {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500/${movie.backdropImagePath}",
                        contentDescription = stringResource(
                            id = R.string.movie_image_description_format,
                            movie.title,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9.0f),
                    )
                }

                val watchActionsTestTag = "${movie.id}_watchActions"
                Row(
                    modifier = Modifier
                        .testTag(watchActionsTestTag)
                        .background(brush = gradientBrush),
                ) {
                    val hasWatched = movie.hasWatched
                    val isOnWatchlist = movie.watchlistId != null
                    Spacer(modifier = Modifier.weight(1.0f))
                    IconButton(
                        onClick = { onWatchedClick(movie.watchlistId, movie.id, !hasWatched) },
                        modifier = Modifier.testTag("watchedButton"),
                    ) {
                        val icon: Int =
                            if (hasWatched) R.drawable.watched else R.drawable.unwatched
                        val description: Int =
                            if (hasWatched) R.string.mark_as_unwatched_icon_description else R.string.mark_as_watched_icon_description
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = stringResource(id = description),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(
                        onClick = { onWatchlistClick(movie.watchlistId, movie.id) },
                        modifier = Modifier.testTag("watchlistButton"),
                    ) {
                        val icon =
                            if (isOnWatchlist) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder
                        val description: Int =
                            if (isOnWatchlist) R.string.remove_from_watchlist_icon_description else R.string.add_to_watchlist_icon_description
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = description),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            )
            Text(
                text = movie.year,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Light,
                ),
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
        modifier = modifier.navigationBarsPadding(),
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
            FilterList(
                titleStringResId = R.string.categories_filter_title,
                displayItems = categories,
                selectedItem = selectedCategoryState,
                onItemSelected = { selectedCategoryState = it },
                itemLabelMapper = CategoryModel::name,
                modifier = Modifier
                    .padding(top = 8.dp),
                testTagPostFix = "Categories",
            )
            FilterList(
                titleStringResId = R.string.genres_filter_title,
                selectedItem = selectedGenreState,
                onItemSelected = { selectedGenreState = it },
                displayItems = genres,
                itemLabelMapper = GenreModel::name,
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
                filter = FilterModel.default(),
                movies = listOf(
                    MovieSummaryModel(
                        id = 49046,
                        backdropImagePath = null,
                        overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
                        title = "All Quiet on the Western Front",
                        year = "2023",
                        watchlistId = null,
                        hasWatched = false,
                    ),
                    MovieSummaryModel(
                        id = 1043141,
                        backdropImagePath = null,
                        overview = "A cold night in December. Ebba waits for the tram to go home after a party, but the ride takes an unexpected turn.",
                        title = "Night Ride",
                        year = "2023",
                        watchlistId = null,
                        hasWatched = false,
                    ),
                ),
                onFilterClick = { },
                onSortClick = { },
                onMovieClick = { },
                onWatchedClick = { _, _, _ -> },
                onWatchlistClick = { _, _ -> },
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
                filter = FilterModel.default(),
                movies = emptyList(),
                onFilterClick = { },
                onSortClick = { },
                onMovieClick = { },
                onWatchedClick = { _, _, _ -> },
                onWatchlistClick = { _, _ -> },
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
                    watchlistId = null,
                    hasWatched = false,
                ),
                onMovieClick = { },
                onWatchedClick = { _, _, _ -> },
                onWatchlistClick = { _, _ -> },
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

@Composable
fun RadioButtonWithLabel(
    isSelected: Boolean,
    label: String,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            role = Role.RadioButton,
            onClick = onSelected,
        ),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
        )
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SortContent(
    sortOrder: SortOrder,
    sortDirection: SortDirection,
    onApply: (SortModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSortOrder by remember { mutableStateOf(sortOrder) }
    var selectedSortDirection by remember { mutableStateOf(sortDirection) }

    Column(
        modifier = modifier
            .navigationBarsPadding(),
    ) {
        Text(
            text = stringResource(id = R.string.sort_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        Text(
            text = stringResource(id = R.string.sort_by),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        )
        RadioButtonWithLabel(
            isSelected = selectedSortOrder == SortOrder.YEAR,
            label = stringResource(id = R.string.year_label),
            onSelected = { selectedSortOrder = SortOrder.YEAR },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        RadioButtonWithLabel(
            isSelected = selectedSortOrder == SortOrder.TITLE,
            label = stringResource(id = R.string.title_label),
            onSelected = { selectedSortOrder = SortOrder.TITLE },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        Text(
            text = stringResource(id = R.string.sort_direction),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        )
        RadioButtonWithLabel(
            isSelected = selectedSortDirection == SortDirection.ASCENDING,
            label = stringResource(id = R.string.ascending_label),
            onSelected = { selectedSortDirection = SortDirection.ASCENDING },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        RadioButtonWithLabel(
            isSelected = selectedSortDirection == SortDirection.DESCENDING,
            label = stringResource(id = R.string.descending_label),
            onSelected = { selectedSortDirection = SortDirection.DESCENDING },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sortContent"),
        ) {
            Button(
                onClick = {
                    onApply(SortModel(selectedSortOrder, selectedSortDirection))
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

@Preview
@Composable
fun SortDialogPreview() {
    OscarsTheme {
        Surface {
            SortContent(
                sortOrder = SortOrder.YEAR,
                sortDirection = SortDirection.DESCENDING,
                onApply = { _ -> },
            )
        }
    }
}
