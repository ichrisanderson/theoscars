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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.common.InteractiveRatingBar
import com.chrisa.theoscars.core.ui.common.TextUtil.prefixWithCeremonyEmoji
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel
import com.chrisa.theoscars.features.movie.domain.models.WatchlistDataModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieScreen(
    viewModel: MovieViewModel,
    onClose: () -> Unit,
    onPlayClicked: (String) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            if (modalSheetState.isVisible) {
                Surface {
                    val data = viewState.watchlistData
                    WatchListContent(
                        watched = data.hasWatched,
                        rating = data.rating,
                        notes = data.notes,
                        onApplyChanges = { watched, rating, notes ->
                            viewModel.updateWatchlistData(watched, rating, notes)
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        },
                    )
                }
            }
        },
    ) {
        MovieContent(
            isLoading = viewState.isLoading,
            movie = viewState.movie,
            watchlistData = viewState.watchlistData,
            onClose = onClose,
            onToggleWatchlist = viewModel::toggleWatchlist,
            onPlayClicked = onPlayClicked,
            onEditClicked = {
                coroutineScope.launch {
                    modalSheetState.show()
                }
            },
        )
    }
}

@Composable
private fun MovieContent(
    isLoading: Boolean,
    movie: MovieDetailModel,
    watchlistData: WatchlistDataModel,
    onClose: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onPlayClicked: (String) -> Unit,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val expandedFabState by remember { derivedStateOf { scrollState.canScrollForward } }

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
        topBar = {
            AppBar(
                isOnWatchlist = watchlistData.isOnWatchlist,
                onToggleWatchlist = onToggleWatchlist,
                onClose = onClose,
            )
        },
        floatingActionButton = {
            if (watchlistData.isOnWatchlist) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "Review")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.RateReview,
                            contentDescription = "Edit Watchlist",
                        )
                    },
                    onClick = onEditClicked,
                    expanded = expandedFabState,
                )
            }
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
    onClose: () -> Unit,
    onToggleWatchlist: () -> Unit,
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
                onClick = onToggleWatchlist,
                modifier = Modifier.testTag("searchButton"),
            ) {
                val icon =
                    if (isOnWatchlist) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(id = R.string.watchlist_icon_description),
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

@Composable
fun WatchListContent(
    watched: Boolean,
    rating: Int,
    notes: String,
    onApplyChanges: (Boolean, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var watchedState by remember { mutableStateOf(watched) }
    var notesState by remember { mutableStateOf(notes) }
    var ratingState by remember { mutableStateOf(rating) }

    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.review_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
        )
        Divider(
            modifier = Modifier
                .padding(bottom = 8.dp),
        )
        WatchedSwitch(
            isSelected = watchedState,
            onSelectionChange = { watchedState = it },
            modifier = Modifier
                .padding(top = 8.dp),
        )
        Rating(
            rating = ratingState,
            onRatingChange = { ratingState = it },
            modifier = Modifier
                .padding(top = 16.dp),
        )
        Notes(
            notes = notesState,
            onNotesChanged = { notesState = it },
            modifier = Modifier
                .padding(top = 8.dp),
        )
        Button(
            onClick = {
                onApplyChanges(watchedState, ratingState, notesState)
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

@Composable
private fun WatchedSwitch(
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("watchedRow")
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
            text = stringResource(id = R.string.watched_label),
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
                .testTag("watchedSwitch")
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun Rating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.rating_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp),
        )
        Spacer(modifier = Modifier.weight(1.0f))
        InteractiveRatingBar(
            rating = rating,
            modifier = Modifier.padding(horizontal = 16.dp),
            onRatingChange = onRatingChange,
        )
    }
}

@Composable
private fun Notes(
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.notes_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        )
        Divider(
            modifier = Modifier
                .alpha(0.3f),
        )
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            minLines = 5,
            maxLines = 10,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("notes"),
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
                    rating = 0,
                    notes = "",
                ),
                onClose = { },
                onToggleWatchlist = { },
                onPlayClicked = { },
                onEditClicked = { },
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
                    rating = 0,
                    notes = "",
                ),
                onClose = { },
                onToggleWatchlist = { },
                onPlayClicked = { },
                onEditClicked = { },
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

@Preview
@Composable
fun WatchlistDialogPreview() {
    OscarsTheme {
        Surface {
            WatchListContent(
                watched = true,
                rating = 3,
                notes = "Great fun",
                onApplyChanges = { _, _, _ -> },
            )
        }
    }
}
