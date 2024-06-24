package com.kgurgul.cpuinfo.di

import com.kgurgul.cpuinfo.components.ComponentsModule
import com.kgurgul.cpuinfo.data.DataModule
import com.kgurgul.cpuinfo.domain.DomainModule
import com.kgurgul.cpuinfo.features.FeaturesModule
import com.kgurgul.cpuinfo.utils.UtilsModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
    startKoin {
        modules(
            ComponentsModule().module,
            DataModule().module,
            DomainModule().module,
            FeaturesModule().module,
            UtilsModule().module,
            viewModelModule,
        )
    }
}