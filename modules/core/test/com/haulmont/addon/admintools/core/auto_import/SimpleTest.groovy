package com.haulmont.addon.admintools.core.auto_import

import com.haulmont.addon.admintools.AdminToolsIntegrationTestSpecification
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.LoadContext

class SimpleTest extends AdminToolsIntegrationTestSpecification {

    def "check integration tests are worked as well"() {
        given:
        FileDescriptor fileDescriptor = metadata.create(FileDescriptor.class)
        fileDescriptor.with {
            name = 'fileDescriptorName'
        }

        when:
        dataManager.commit(fileDescriptor)
        FileDescriptor reloadedFD = dataManager.load(LoadContext.create(FileDescriptor.class).setId(fileDescriptor.id))

        then:
        fileDescriptor == reloadedFD
    }

}
