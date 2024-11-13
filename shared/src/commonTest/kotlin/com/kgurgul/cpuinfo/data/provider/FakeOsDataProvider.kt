package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.TestData

class FakeOsDataProvider : IOsDataProvider {

    override suspend fun getData(): List<Pair<String, String>> {
        return TestData.itemRowData
    }
}
