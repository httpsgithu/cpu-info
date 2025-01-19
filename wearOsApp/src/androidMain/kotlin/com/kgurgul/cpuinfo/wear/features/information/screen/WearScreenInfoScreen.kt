@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.screen
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearScreenInfoScreen(
    viewModel: ScreenInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearScreenInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun WearScreenInfoScreen(
    uiState: ScreenInfoViewModel.UiState,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.screen),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(
                uiState.items,
                key = { itemValue -> itemValue.getKey() },
            ) { itemValue ->
                WearCpuChip(
                    label = itemValue.getName(),
                    secondaryLabel = itemValue.getValue(),
                )
            }
        }
    }
}
