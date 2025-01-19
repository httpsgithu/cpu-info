package com.kgurgul.cpuinfo.tv.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import com.kgurgul.cpuinfo.features.settings.getTemperatureUnit
import com.kgurgul.cpuinfo.features.settings.getThemeName
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cancel
import com.kgurgul.cpuinfo.shared.general
import com.kgurgul.cpuinfo.shared.pref_theme
import com.kgurgul.cpuinfo.shared.pref_theme_choose
import com.kgurgul.cpuinfo.shared.temperature_unit
import com.kgurgul.cpuinfo.tv.ui.components.TvAlertDialog
import com.kgurgul.cpuinfo.tv.ui.components.TvButton
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingLarge
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvSettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvSettingsScreen(
        uiState = uiState,
        onTemperatureOptionClicked = viewModel::setTemperatureUnit,
        onThemeOptionClicked = viewModel::setTheme,
    )
}

@Composable
fun TvSettingsScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureOptionClicked: (Int) -> Unit,
    onThemeOptionClicked: (String) -> Unit,
) {
    Scaffold { paddingValues ->
        var isTemperatureDialogVisible by remember { mutableStateOf(false) }
        var isThemeDialogVisible by remember { mutableStateOf(false) }
        SettingsList(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onThemeItemClicked = { isThemeDialogVisible = true },
            onTemperatureItemClicked = { isTemperatureDialogVisible = true },
        )
        TemperatureUnitDialog(
            isDialogVisible = isTemperatureDialogVisible,
            onDismissRequest = { isTemperatureDialogVisible = false },
            currentSelection = uiState.temperatureUnit,
            options = uiState.temperatureDialogOptions,
            onOptionClicked = onTemperatureOptionClicked,
        )
        ThemeDialog(
            isDialogVisible = isThemeDialogVisible,
            onDismissRequest = { isThemeDialogVisible = false },
            currentSelection = uiState.theme,
            options = uiState.themeDialogOptions,
            onOptionClicked = onThemeOptionClicked,
        )
    }
}

@Composable
private fun SettingsList(
    uiState: SettingsViewModel.UiState,
    onThemeItemClicked: () -> Unit,
    onTemperatureItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingMedium),
        modifier = modifier,
    ) {
        item(key = "__generalHeader") {
            Text(
                text = stringResource(Res.string.general),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.requiredSize(spacingMedium))
        }
        item(key = "__themeItem") {
            SettingsItem(
                title = stringResource(Res.string.pref_theme),
                subtitle = getThemeName(option = uiState.theme),
                onClick = onThemeItemClicked,
            )
        }
        item(key = "__temperatureItem") {
            SettingsItem(
                title = stringResource(Res.string.temperature_unit),
                subtitle = getTemperatureUnit(option = uiState.temperatureUnit),
                onClick = onTemperatureItemClicked,
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    TvListItem(
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacingMedium)
                .padding(start = spacingLarge),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TemperatureUnitDialog(
    isDialogVisible: Boolean,
    onDismissRequest: () -> Unit,
    currentSelection: Int,
    options: ImmutableList<Int>,
    onOptionClicked: (Int) -> Unit,
) {
    if (isDialogVisible) {
        TvAlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(Res.string.temperature_unit))
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        TvListItem(
                            onClick = {
                                onOptionClicked(option)
                                onDismissRequest()
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacingSmall),
                            ) {
                                RadioButton(
                                    selected = option == currentSelection,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.tertiary,
                                    ),
                                )
                                Text(
                                    text = getTemperatureUnit(option = option),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TvButton(
                    onClick = onDismissRequest,
                ) {
                    androidx.tv.material3.Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ThemeDialog(
    isDialogVisible: Boolean,
    onDismissRequest: () -> Unit,
    currentSelection: String,
    options: ImmutableList<String>,
    onOptionClicked: (String) -> Unit,
) {
    if (isDialogVisible) {
        TvAlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(Res.string.pref_theme_choose))
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        TvListItem(
                            onClick = {
                                onOptionClicked(option)
                                onDismissRequest()
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacingSmall),
                            ) {
                                RadioButton(
                                    selected = option == currentSelection,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.tertiary,
                                    ),
                                )
                                Text(
                                    text = getThemeName(option = option),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TvButton(
                    onClick = onDismissRequest,
                ) {
                    androidx.tv.material3.Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
}
