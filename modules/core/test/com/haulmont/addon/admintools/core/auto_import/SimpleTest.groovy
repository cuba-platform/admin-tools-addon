/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
