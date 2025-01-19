package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.IPackageNameProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider

class GetPackageNameInteractor(
    dispatchersProvider: IDispatchersProvider,
    private val packageNameProvider: IPackageNameProvider,
) : ResultInteractor<Unit, String>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit): String {
        return packageNameProvider.getPackageName()
    }
}
