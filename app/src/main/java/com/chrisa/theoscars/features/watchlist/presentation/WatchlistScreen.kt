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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.watchlist.domain.models.WatchlistMovieModel

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onMovieClick: (Long) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    WatchlistScreenContent(
        movies = viewState.movies,
        onMovieClick = onMovieClick,
        modifier = Modifier.testTag("watchlistScreenContent"),
    )
}

@Composable
private fun WatchlistScreenContent(
    movies: List<WatchlistMovieModel>,
    onMovieClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (movies.isEmpty()) {
        EmptyWatchlistResults(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(items = movies, key = { it.movieId }) { model ->
                WatchlistResultCard(
                    movieModel = model,
                    onClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
private fun EmptyWatchlistResults(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(32.dp),
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(id = R.string.empty_watchlist_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun WatchlistResultCard(
    movieModel: WatchlistMovieModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .clickable { onClick(movieModel.movieId) }
            .testTag("movieCard_${movieModel.movieId}"),
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    text = movieModel.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = movieModel.year.prefixWithCeremonyEmoji(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun WatchlistEmptyScreenPreview() {
    OscarsTheme {
        Surface {
            WatchlistScreenContent(
                movies = emptyList(),
                onMovieClick = { },
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
