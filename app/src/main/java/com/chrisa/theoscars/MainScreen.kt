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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chrisa.theoscars.core.ui.common.ComingSoon
import com.chrisa.theoscars.features.home.presentation.HomeScreen
import com.chrisa.theoscars.features.home.presentation.HomeViewModel

@Composable
fun MainScreen(
    onMovieClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val tabs = remember { MainTabs.values() }
    Scaffold(
        bottomBar = {
            MainBottomBar(navController, tabs)
        },
    ) { innerPaddingModifier ->
        NavHost(
            navController = navController,
            startDestination = MainDestinations.HOME,
            modifier = Modifier.padding(innerPaddingModifier),
        ) {
            composable(MainDestinations.HOME) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onMovieClick = onMovieClick,
                    onSearchClick = onSearchClick,
                )
            }
            composable(MainDestinations.WATCHLIST) {
                ComingSoon()
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    navController: NavHostController,
    tabs: Array<MainTabs>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        tabs.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        screen.icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(screen.title)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
            )
        }
    }
}

private object MainDestinations {
    const val HOME = "home"
    const val WATCHLIST = "watchlist"
}

private enum class MainTabs(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String,
) {
    HOME(R.string.home_tab, Icons.Default.Home, MainDestinations.HOME),
    WATCHLIST(
        R.string.watchlist_tab,
        Icons.Default.CollectionsBookmark,
        MainDestinations.WATCHLIST,
    ),
}
