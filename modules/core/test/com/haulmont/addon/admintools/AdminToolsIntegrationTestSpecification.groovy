package com.haulmont.addon.admintools

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class AdminToolsIntegrationTestSpecification extends Specification {

    @ClassRule @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    Metadata metadata
    DataManager dataManager

    void setup() {
        metadata = AppBeans.get(Metadata)
        dataManager = AppBeans.get(DataManager)
    }

//    void cleanup() {
//        builder.cleanUp()
//    }

}
