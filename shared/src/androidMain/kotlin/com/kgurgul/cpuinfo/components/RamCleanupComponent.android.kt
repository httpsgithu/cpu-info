package com.kgurgul.cpuinfo.components

import android.os.Build

actual class RamCleanupComponent actual constructor() {

    actual fun cleanup() {
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }

    actual fun isCleanupActionAvailable(): Boolean {
        return Build.VERSION.SDK_INT < 24
    }
}
