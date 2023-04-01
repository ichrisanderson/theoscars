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

package com.chrisa.theoscars.features.movie.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieScreen(
    viewModel: MovieViewModel,
    onClose: () -> Unit,
    onPlayClicked: (String) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    MovieContent(
        isLoading = viewState.isLoading,
        movie = viewState.movie,
        watchlistData = viewState.watchlistData,
        onClose = onClose,
        onToggleWatchlist = viewModel::toggleWatchlist,
        onToggleWatched = viewModel::toggleWatched,
        onPlayClicked = onPlayClicked,
    )
}

@Composable
private fun MovieContent(
    isLoading: Boolean,
    movie: MovieDetailModel,
    watchlistData: WatchlistDataModel,
    onClose: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleWatched: () -> Unit,
    onPlayClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
        topBar = {
            AppBar(
                isOnWatchlist = watchlistData.isOnWatchlist,
                hasWatched = watchlistData.hasWatched,
                onToggleWatchlist = onToggleWatchlist,
                onToggleWatched = onToggleWatched,
                onClose = onClose,
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            } else {
                Column(
                    modifier
                        .verticalScroll(scrollState)
                        .testTag("movieContent_${movie.id}"),
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9.0f),
                    ) {
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
                        Button(
                            onClick = { onPlayClicked(movie.youTubeVideoKey.orEmpty()) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black.copy(alpha = 0.6f),
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("playButton"),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                tint = Color.White.copy(alpha = 0.9f),
                                contentDescription = stringResource(id = R.string.play_button_description),
                                modifier = Modifier
                                    .size(48.dp),
                            )
                        }
                    }
                    Divider()
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    )
                    Text(
                        text = movie.year.prefixWithCeremonyEmoji(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    )
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    )
                    HeaderSection(
                        R.string.nominations_title,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp,
                        ),
                    ) {
                        Column {
                            movie.nominations.forEach {
                                Nomination(
                                    category = it.category,
                                    name = it.name,
                                    winner = it.winner,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    isOnWatchlist: Boolean,
    hasWatched: Boolean,
    onClose: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleWatched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(top = 0.dp),
        title = { },
        navigationIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("closeButton"),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = stringResource(id = R.string.close_button_description),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onToggleWatched,
                modifier = Modifier.testTag("watchedButton"),
            ) {
                val icon: Int =
                    if (hasWatched) R.drawable.watched else R.drawable.unwatched
                val description: Int =
                    if (hasWatched) R.string.unmark_as_watched_icon_description else R.string.mark_as_watched_icon_description
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = stringResource(id = description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onToggleWatchlist,
                modifier = Modifier.testTag("watchlistButton"),
            ) {
                val icon =
                    if (isOnWatchlist) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder
                val description: Int =
                    if (hasWatched) R.string.remove_from_to_watchlist_icon_description else R.string.add_to_watchlist_icon_description
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(id = description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}

@Composable
fun HeaderSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .paddingFromBaseline(top = 32.dp, bottom = 12.dp),
        )
        content()
    }
}

@Composable
fun Nomination(
    category: String,
    name: String,
    modifier: Modifier = Modifier,
    winner: Boolean,
) {
    Column(
        modifier = modifier,
    ) {
        val annotatedString = buildAnnotatedString {
            append(category)
            if (winner) {
                append(" ")
                appendInlineContent(id = "winnerIcon")
            }
        }
        val inlineContentMap = mapOf(
            "winnerIcon" to InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter),
            ) {
                Image(
                    painter = painterResource(R.drawable.winner_badge),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = stringResource(id = R.string.winner_badge_description),
                )
            },
        )
        Text(
            text = annotatedString,
            inlineContent = inlineContentMap,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0)
@Composable
fun MovieLoadingContentPreview() {
    OscarsTheme {
        Surface {
            MovieContent(
                isLoading = true,
                movie = MovieDetailModel(
                    id = 0L,
                    backdropImagePath = null,
                    overview = "",
                    title = "",
                    year = "",
                    youTubeVideoKey = null,
                    nominations = emptyList(),
                ),
                watchlistData = WatchlistDataModel(
                    movieId = -1,
                    isOnWatchlist = false,
                    hasWatched = false,
                ),
                onClose = { },
                onToggleWatchlist = { },
                onToggleWatched = { },
                onPlayClicked = { },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0)
@Composable
fun MovieContentPreview() {
    OscarsTheme {
        Surface {
            MovieContent(
                isLoading = false,
                movie = MovieDetailModel(
                    id = 49046,
                    backdropImagePath = "/mqsPyyeDCBAghXyjbw4TfEYwljw.jpg",
                    overview = "Paul Baumer and his friends Albert and Muller, egged on by romantic dreams of heroism, voluntarily enlist in the German army. Full of excitement and patriotic fervour, the boys enthusiastically march into a war they believe in. But once on the Western Front, they discover the soul-destroying horror of World War I.",
                    title = "All Quiet on the Western Front",
                    year = "2022",
                    youTubeVideoKey = "hf8EYbVxtCY",
                    nominations = listOf(
                        NominationModel(
                            category = "Cinematography",
                            name = "James Friend",
                            winner = false,
                        ),
                        NominationModel(
                            category = "International Feature Film",
                            name = "Germany",
                            winner = true,
                        ),
                    ),
                ),
                watchlistData = WatchlistDataModel(
                    movieId = -1,
                    isOnWatchlist = false,
                    hasWatched = false,
                ),
                onClose = { },
                onToggleWatchlist = { },
                onToggleWatched = { },
                onPlayClicked = { },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0)
@Composable
fun HeaderSectionPreview() {
    OscarsTheme {
        Surface {
            HeaderSection(
                title = R.string.nominations_title,
            ) {
                Nomination(
                    category = "Best Picture",
                    name = "Steven Speilburg, Producer",
                    winner = false,
                )
            }
        }
    }
}
