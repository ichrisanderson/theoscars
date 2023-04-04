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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chrisa.theoscars.features.home.presentation.FilterContent
import com.chrisa.theoscars.features.home.presentation.HomeScreen
import com.chrisa.theoscars.features.home.presentation.HomeViewModel
import com.chrisa.theoscars.features.watchlist.presentation.WatchlistScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    onMovieClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    navController: NavHostController = rememberNavController(),
) {
    val homeViewState by homeViewModel.viewState.collectAsState()
    val tabs = remember { MainTabs.values() }
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTabDestination = navBackStackEntry?.destination

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            if (modalSheetState.isVisible) {
                val filterModel = homeViewState.filterModel
                Surface {
                    FilterContent(
                        categories = filterModel.categories,
                        selectedCategory = filterModel.selectedCategory,
                        genres = filterModel.genres,
                        selectedGenre = filterModel.selectedGenre,
                        currentStartYear = filterModel.startYearString,
                        currentEndYear = filterModel.endYearString,
                        winnersOnly = filterModel.winnersOnly,
                        onApplySelection = { newFilter ->
                            homeViewModel.updateFilter(newFilter)
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        },
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    isFilterVisible = currentTabDestination?.route == MainDestinations.HOME,
                    onSearchClick = onSearchClick,
                    onFilterClick = {
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    },
                )
            },
            bottomBar = {
                BottomBar(navController, tabs)
            },
        ) { innerPaddingModifier ->
            NavHost(
                navController = navController,
                startDestination = MainDestinations.HOME,
                modifier = Modifier.padding(innerPaddingModifier),
            ) {
                composable(MainDestinations.HOME) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onMovieClick = onMovieClick,
                    )
                }
                composable(MainDestinations.WATCHLIST) {
                    WatchlistScreen(
                        viewModel = hiltViewModel(),
                        onMovieClick = onMovieClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    isFilterVisible: Boolean,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
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
                contentDescription = stringResource(id = R.string.app_logo_description),
            )
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("searchButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_icon_description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (isFilterVisible) {
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
            }
        },
    )
}

@Composable
private fun BottomBar(
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
