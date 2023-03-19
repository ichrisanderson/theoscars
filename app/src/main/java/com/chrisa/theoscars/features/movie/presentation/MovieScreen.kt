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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chrisa.theoscars.R
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.movie.domain.models.MovieDetailModel
import com.chrisa.theoscars.features.movie.domain.models.NominationModel

@Composable
fun MovieScreen(
    viewModel: MovieViewModel,
    onClose: () -> Unit,
    onPlayClicked: (String) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    Scaffold(
        topBar = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                )
            }
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            MovieContent(
                movie = viewState.movie,
                onPlayClicked = onPlayClicked,
            )
        }
    }
}

@Composable
private fun MovieContent(
    movie: MovieDetailModel?,
    onPlayClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (movie == null) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    } else {
        Column(
            modifier.verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9.0f),
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500/${movie.backdropImagePath}",
                    contentDescription = null,
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
                        .align(Alignment.Center),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        tint = Color.White.copy(alpha = 0.9f),
                        contentDescription = null,
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
                text = movie.year,
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
                        Nomination(category = it.category, name = it.name)
                    }
                }
            }
        }
    }
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
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = category,
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
                movie = null,
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
                            winner = false,
                        ),
                    ),
                ),
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
                )
            }
        }
    }
}
