package com.kgurgul.cpuinfo.components

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val componentsModule = module {
    factoryOf(::RamCleanupComponent) bind IRamCleanupComponent::class
}
