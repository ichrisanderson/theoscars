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

package com.chrisa.theoscars.features.watchlist.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onMovieClick: (Long) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    WatchlistScreenContent(
        moviesToWatch = viewState.moviesToWatch,
        moviesWatched = viewState.moviesWatched,
        hasSelectedIds = viewState.hasSelectedIds,
        selectedIds = viewState.selectedIds,
        onMovieClick = onMovieClick,
        onMovieLongClick = viewModel::toggleItemSelection,
    )
}

@Composable
private fun WatchlistScreenContent(
    moviesToWatch: List<WatchlistMovieModel>,
    moviesWatched: List<WatchlistMovieModel>,
    hasSelectedIds: Boolean,
    selectedIds: Set<Long>,
    onMovieClick: (Long) -> Unit,
    onMovieLongClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.testTag("watchlistItems"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        item(key = "toWatchListTitle") {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.to_watch_format, moviesToWatch.size),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item {
            Divider(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .alpha(0.3f),
            )
        }
        if (moviesToWatch.isEmpty()) {
            item(key = "emptyWatchlist") {
                EmptyWatchlist()
            }
        }
        items(items = moviesToWatch, key = { it.movieId }) { model ->
            WatchlistResultCard(
                movieModel = model,
                isInSelectionMode = hasSelectedIds,
                isSelected = selectedIds.contains(model.id),
                onClick = onMovieClick,
                onLongClick = onMovieLongClick,
            )
        }
        item(key = "watchedListTitle") {
            Text(
                modifier = Modifier
                    .padding(top = 32.dp),
                text = stringResource(id = R.string.watched_format, moviesWatched.size),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item {
            Divider(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .alpha(0.3f),
            )
        }
        if (moviesWatched.isEmpty()) {
            item(key = "emptyWatchedList") {
                EmptyWatchedList()
            }
        }
        items(items = moviesWatched, key = { it.movieId }) { model ->
            WatchlistResultCard(
                movieModel = model,
                isInSelectionMode = hasSelectedIds,
                isSelected = selectedIds.contains(model.id),
                onClick = onMovieClick,
                onLongClick = onMovieLongClick,
            )
        }
    }
}

@Composable
private fun EmptyWatchlist(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = stringResource(id = R.string.empty_watch_list_icon_description),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp),
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(id = R.string.empty_watch_list_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun EmptyWatchedList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.unwatched),
            contentDescription = stringResource(id = R.string.empty_watched_list_icon_description),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp),
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(id = R.string.empty_watched_list_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchlistResultCard(
    movieModel: WatchlistMovieModel,
    isInSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: (Long) -> Unit,
    onLongClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (!isInSelectionMode) {
                        onClick(movieModel.movieId)
                    } else {
                        onLongClick(movieModel.id)
                    }
                },
                onLongClick = {
                    onLongClick(movieModel.id)
                },
            )
            .testTag("watchListMovieCard_${movieModel.movieId}"),
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
            Box {
                if (movieModel.posterImagePath == null) {
                    Image(
                        painter = painterResource(R.drawable.show_reel),
                        colorFilter = ColorFilter.tint(Color.LightGray),
                        contentDescription = stringResource(
                            id = R.string.movie_image_default_format,
                            movieModel.title,
                        ),
                        modifier = Modifier
                            .width(96.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .aspectRatio(3 / 4.0f)
                            .padding(16.dp),
                    )
                } else {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500/${movieModel.posterImagePath}",
                        contentDescription = stringResource(
                            id = R.string.movie_image_default_format,
                            movieModel.title,
                        ),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(3 / 4.0f),
                    )
                }
                if (isSelected) {
                    Column(
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(3 / 4.0f)
                            .background(Color.Black.copy(alpha = 0.66f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(
                                id = R.string.movie_selected_description_format,
                                movieModel.title,
                            ),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .testTag("movieSelectedIndicator_${movieModel.movieId}")
                                .size(48.dp),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp)
                    .testTag("watchlistMovieData"),
            ) {
                Text(
                    text = movieModel.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = movieModel.year,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Light,
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun WatchlistScreenPreview() {
    OscarsTheme {
        Surface {
            WatchlistScreenContent(
                moviesToWatch = listOf(
                    WatchlistMovieModel(
                        id = 1,
                        movieId = 1,
                        posterImagePath = null,
                        title = "All Quiet on the Western Front",
                        year = "2023",
                    ),
                    WatchlistMovieModel(
                        id = 2,
                        movieId = 2,
                        posterImagePath = null,
                        title = "Avatar",
                        year = "2023",
                    ),
                    WatchlistMovieModel(
                        id = 2,
                        movieId = 3,
                        posterImagePath = null,
                        title = "Everything Everywhere All At Once",
                        year = "2023",
                    ),
                ),
                moviesWatched = emptyList(),
                hasSelectedIds = false,
                selectedIds = emptySet(),
                onMovieClick = { },
                onMovieLongClick = { },
            )
        }
    }
}

@Preview
@Composable
fun WatchlistEmptyScreenPreview() {
    OscarsTheme {
        Surface {
            WatchlistScreenContent(
                moviesToWatch = emptyList(),
                moviesWatched = emptyList(),
                hasSelectedIds = false,
                selectedIds = emptySet(),
                onMovieClick = { },
                onMovieLongClick = { },
            )
        }
    }
}

@Preview
@Composable
fun WatchlistResultCardPreview() {
    OscarsTheme {
        Surface {
            WatchlistResultCard(
                movieModel = WatchlistMovieModel(
                    id = 1,
                    movieId = 1234,
                    posterImagePath = null,
                    title = "All Quiet on the Western Front",
                    year = "2023",
                ),
                isInSelectionMode = false,
                isSelected = false,
                onClick = { },
                onLongClick = { },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Preview
@Composable
fun WatchlistResultCardSelectedPreview() {
    OscarsTheme {
        Surface {
            WatchlistResultCard(
                movieModel = WatchlistMovieModel(
                    id = 1,
                    movieId = 1234,
                    posterImagePath = null,
                    title = "All Quiet on the Western Front",
                    year = "2023",
                ),
                isInSelectionMode = false,
                isSelected = true,
                onClick = { },
                onLongClick = { },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
