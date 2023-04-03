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

package com.chrisa.theoscars

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chrisa.theoscars.features.movie.presentation.MovieScreen
import com.chrisa.theoscars.features.movie.presentation.MovieViewModel
import com.chrisa.theoscars.features.search.presentation.SearchScreen
import com.chrisa.theoscars.features.search.presentation.SearchViewModel

@Composable
fun AppNavGraph(
    activity: Activity,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MAIN,
        modifier = modifier,
    ) {
        composable(AppDestinations.MAIN) {
            MainScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie/$movieId")
                },
                onSearchClick = {
                    navController.navigate(AppDestinations.SEARCH)
                },
            )
        }
        composable(
            AppDestinations.MOVIE_DETAIL,
            arguments = listOf(navArgument("movieId") { type = NavType.LongType }),
        ) {
            val viewModel = hiltViewModel<MovieViewModel>()
            MovieScreen(
                viewModel = viewModel,
                onClose = {
                    navController.popBackStack()
                },
                onPlayClicked = { videoId ->
                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=$videoId"),
                        ),
                    )
                },
            )
        }
        composable(AppDestinations.SEARCH) {
            val viewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(
                viewModel,
                onMovieClick = { movieId ->
                    navController.navigate("movie/$movieId")
                },
                onClose = {
                    navController.popBackStack()
                },
            )
        }
    }
}

object AppDestinations {
    const val MAIN = "main"
    const val MOVIE_DETAIL = "movie/{movieId}"
    const val SEARCH = "search"
}
