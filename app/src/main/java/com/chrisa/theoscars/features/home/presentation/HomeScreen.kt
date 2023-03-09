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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.home.domain.models.MovieSummaryModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Long) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
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
            modifier = Modifier.alpha(contentAlpha),
            topPadding = contentTopPadding,
            movies = viewState.movies,
            onMovieClick = onMovieClick,
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    movies: List<MovieSummaryModel>,
    onMovieClick: (Long) -> Unit,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.padding(top = topPadding))
        AppBar()
        LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
            items(items = movies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.smallTopAppBarColors(
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
