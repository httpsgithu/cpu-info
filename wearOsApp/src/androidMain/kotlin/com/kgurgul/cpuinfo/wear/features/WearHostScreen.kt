@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.navigation
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.information
import com.kgurgul.cpuinfo.shared.menu
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import com.kgurgul.cpuinfo.utils.sharedViewModel
import com.kgurgul.cpuinfo.wear.features.applications.WearApplicationsScreen
import com.kgurgul.cpuinfo.wear.features.information.WearInfoContainerScreen
import com.kgurgul.cpuinfo.wear.features.settings.WearSettingsScreen
import com.kgurgul.cpuinfo.wear.features.settings.WearTemperatureUnitPickerScreen
import com.kgurgul.cpuinfo.wear.features.temperature.WearTemperatureScreen
import com.kgurgul.cpuinfo.wear.theme.WearAppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearHostScreen(
    viewModel: HostViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val navController = rememberSwipeDismissableNavController()
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val swipeDismissableNavHostState = rememberSwipeDismissableNavHostState(
        swipeToDismissBoxState = swipeToDismissBoxState,
    )
    WearAppTheme {
        AppScaffold(
            modifier = Modifier.background(MaterialTheme.colors.background),
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = WearHostScreen.Menu.route,
                state = swipeDismissableNavHostState,
            ) {
                composable(WearHostScreen.Menu.route) {
                    MenuScreen(
                        uiState = uiState,
                        onInformationClicked = {
                            navController.navigate(WearHostScreen.Information.route)
                        },
                        onApplicationsClicked = {
                            navController.navigate(WearHostScreen.Applications.route)
                        },
                        onTemperatureClicked = {
                            navController.navigate(WearHostScreen.Temperature.route)
                        },
                        onSettingsClicked = {
                            navController.navigate(WearHostScreen.Settings.route)
                        },
                    )
                }
                composable(WearHostScreen.Information.route) {
                    WearInfoContainerScreen()
                }
                composable(WearHostScreen.Applications.route) {
                    WearApplicationsScreen(
                        swipeToDismissBoxState = swipeToDismissBoxState,
                    )
                }
                composable(WearHostScreen.Temperature.route) {
                    WearTemperatureScreen()
                }
                navigation(
                    startDestination = WearHostScreen.Settings.List.route,
                    route = WearHostScreen.Settings.route,
                ) {
                    composable(WearHostScreen.Settings.List.route) {
                        WearSettingsScreen(
                            viewModel = it.sharedViewModel(navController),
                            onTemperatureUnitClicked = {
                                navController.navigate(
                                    WearHostScreen.Settings.TemperatureUnitPicker.route
                                )
                            },
                        )
                    }
                    composable(WearHostScreen.Settings.TemperatureUnitPicker.route) {
                        WearTemperatureUnitPickerScreen(
                            viewModel = it.sharedViewModel(navController),
                            onTemperatureUnitSelected = {
                                navController.popBackStack()
                            },
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun MenuScreen(
    uiState: HostViewModel.UiState,
    onInformationClicked: () -> Unit,
    onApplicationsClicked: () -> Unit,
    onTemperatureClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )
    ScreenScaffold(
        scrollState = columnState,
    ) {
        ScalingLazyColumn(
            columnState = columnState,
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.menu),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            item {
                Chip(
                    label = stringResource(Res.string.information),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cpu),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onInformationClicked,
                )
            }
            if (uiState.isApplicationSectionVisible) {
                item {
                    Chip(
                        label = stringResource(Res.string.applications),
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_android),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ChipDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        },
                        onClick = onApplicationsClicked,
                    )
                }
            }
            item {
                Chip(
                    label = stringResource(Res.string.temp),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_temperature),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onTemperatureClicked,
                )
            }
            item {
                Chip(
                    label = stringResource(Res.string.settings),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onSettingsClicked,
                )
            }
        }
    }
}

sealed class WearHostScreen(val route: String) {
    data object Menu : WearHostScreen("menu")
    data object Information : WearHostScreen("information")
    data object Applications : WearHostScreen("applications")
    data object Temperature : WearHostScreen("temperature")
    data object Settings : WearHostScreen("settings") {
        data object List : WearHostScreen("settings_list")
        data object TemperatureUnitPicker : WearHostScreen("temperature_unit_picker")
    }
}
