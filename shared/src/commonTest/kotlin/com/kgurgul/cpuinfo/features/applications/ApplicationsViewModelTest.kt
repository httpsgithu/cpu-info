package com.kgurgul.cpuinfo.features.applications

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.FakeApplicationsDataProvider
import com.kgurgul.cpuinfo.data.provider.FakePackageNameProvider
import com.kgurgul.cpuinfo.domain.action.FakeExternalAppAction
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_open
import com.kgurgul.cpuinfo.shared.cpu_open
import com.kgurgul.cpuinfo.shared.cpu_uninstall
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class ApplicationsViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeApplicationsDataProvider = FakeApplicationsDataProvider()
    private val applicationsDataObservable = ApplicationsDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        applicationsDataProvider = fakeApplicationsDataProvider,
    )
    private val fakePackageNameProvider = FakePackageNameProvider()
    private val getPackageNameInteractor = GetPackageNameInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        packageNameProvider = fakePackageNameProvider,
    )
    private val testUserPreferences = TestData.userPreferences
    private val fakeUserPreferencesFlow = flowOf(testUserPreferences)
    private val fakeUserPreferencesRepository = FakeUserPreferencesRepository(
        preferencesFlow = fakeUserPreferencesFlow
    )
    private val fakeExternalAppAction = FakeExternalAppAction()

    private lateinit var viewModel: ApplicationsViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        fakeUserPreferencesRepository.reset()
        fakeExternalAppAction.reset()
        viewModel = ApplicationsViewModel(
            applicationsDataObservable = applicationsDataObservable,
            getPackageNameInteractor = getPackageNameInteractor,
            userPreferencesRepository = fakeUserPreferencesRepository,
            externalAppAction = fakeExternalAppAction,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun loadInitialData() = runTest {
        val expectedUiState = ApplicationsViewModel.UiState(
            withSystemApps = testUserPreferences.withSystemApps,
            isSortAscending = testUserPreferences.isApplicationsSortingAscending,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }

    @Test
    fun handleApplicationsResult() = runTest {
        fakeApplicationsDataProvider.installedApplications = TestData.extendedApplicationsData
        val expectedUiState = ApplicationsViewModel.UiState(
            withSystemApps = testUserPreferences.withSystemApps,
            isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            applications = TestData.extendedApplicationsData.toImmutableList(),
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }

    @Test
    fun onSortOrderChangeClicked() {
        viewModel.onSortOrderChange(false)

        assertTrue(fakeUserPreferencesRepository.isSetApplicationsSortingOrderCalled)
    }

    @Test
    fun onSystemAppsSwitched() {
        viewModel.onSystemAppsSwitched(false)

        assertTrue(fakeUserPreferencesRepository.isSetApplicationsWithSystemAppsCalled)
    }

    @Test
    fun onNativeLibsClicked() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("test").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("src/test/resources").toImmutableList(),
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onNativeLibsClicked(listOf("test"))
            viewModel.onNativeLibsClicked(listOf("src/test/resources"))

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onNativeLibsDialogDismissed() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("src/test/resources").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onNativeLibsClicked(listOf("src/test/resources"))
            viewModel.onNativeLibsDialogDismissed()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onNativeLibsNameClicked() {
        viewModel.onNativeLibsNameClicked("test")

        assertTrue(fakeExternalAppAction.isSearchOnWebCalled)
    }

    @Test
    fun onAppUninstallClicked() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = Res.string.cpu_uninstall,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo.debug")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onAppSettingsClicked() {
        viewModel.onAppSettingsClicked("com.kgurgul.cpuinfo.debug")

        assertTrue(fakeExternalAppAction.isOpenSettingsCalled)
    }

    @Test
    fun onSnackbarDismissed() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = Res.string.cpu_uninstall,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
            viewModel.onSnackbarDismissed()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onCannotOpenApp() = runTest {
        fakeExternalAppAction.isLaunchSuccess = false
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = Res.string.app_open,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onApplicationClicked("com.kgurgul.cpuinfo.debug")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onCurrentApplicationClicked() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = Res.string.cpu_open,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onApplicationClicked("com.kgurgul.cpuinfo")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onRefreshApplicationsClicked() = runTest {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                isLoading = true,
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )

        viewModel.uiStateFlow.test {
            viewModel.onRefreshApplications()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }
}
