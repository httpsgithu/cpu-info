package com.kgurgul.cpuinfo.domain.action

import org.koin.core.component.KoinComponent

actual class ExternalAppAction actual constructor() : IExternalAppAction, KoinComponent {

    actual override fun launch(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun openSettings(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun uninstall(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}
