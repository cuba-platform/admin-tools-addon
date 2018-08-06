package com.haulmont.addon.admintools

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import org.junit.Before
import org.junit.ClassRule

class AdminToolsIntegrationTest {

    @ClassRule
    public static AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    Metadata metadata
    DataManager dataManager

    @Before
    void setUp() {
        metadata = AppBeans.get(Metadata)
        dataManager = AppBeans.get(DataManager)
    }

//    void cleanup() {
//        builder.cleanUp()
//    }

}
