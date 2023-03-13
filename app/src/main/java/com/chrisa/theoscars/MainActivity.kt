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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chrisa.theoscars.core.ui.theme.OscarsTheme
import com.chrisa.theoscars.features.home.presentation.HomeScreen
import com.chrisa.theoscars.features.home.presentation.HomeViewModel
import com.chrisa.theoscars.features.movie.presentation.MovieScreen
import com.chrisa.theoscars.features.movie.presentation.MovieViewModel
import com.chrisa.theoscars.features.search.presentation.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OscarsTheme {
                val navController = rememberNavController()
                OscarsApp(navController)
            }
        }
    }

    @Composable
    private fun OscarsApp(navController: NavHostController) {
        Scaffold { innerPaddingModifier ->
            NavGraph(navController, Modifier.padding(innerPaddingModifier))
        }
    }

    @Composable
    private fun NavGraph(
        navController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME,
            modifier = modifier,
        ) {
            composable(AppDestinations.HOME) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
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
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=$videoId"),
                            ),
                        )
                    },
                )
            }
            composable(AppDestinations.SEARCH) {
                SearchScreen(
                    onClose = {
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}

private object AppDestinations {
    const val HOME = "home"
    const val MOVIE_DETAIL = "movie/{movieId}"
    const val SEARCH = "search"
}
