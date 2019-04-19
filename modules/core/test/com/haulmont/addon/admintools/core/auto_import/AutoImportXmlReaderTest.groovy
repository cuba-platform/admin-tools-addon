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

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.global.auto_import.AutoImportXmlReader
import com.haulmont.addon.admintools.global.auto_import.dto.AutoImportFileDescriptor
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.sys.AppContext
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import static com.haulmont.addon.admintools.global.auto_import.AutoImportXmlReader.AUTOIMPORT_CONFIG

class AutoImportXmlReaderTest extends Specification {

    @ClassRule
    @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImportXmlReader delegate

    String validConfig = 'com/haulmont/addon/admintools/core/auto_import/test_config/auto-import.xml'
    String emptyConfig = 'com/haulmont/addon/admintools/core/auto_import/test_config/empty.xml'
    String notExistConfig = 'not/exist'

    void setup() {
        delegate = AppBeans.get(AutoImportXmlReader)
    }

    def "thrown the exception if a config is not exist"() {
        when:
        AppContext.properties.remove(AUTOIMPORT_CONFIG)
        delegate.getFileDescriptors()

        then:
        thrown(IllegalStateException.class)
    }

    def "thrown exception if a config contains not exist path to auto-import xml"() {
        when:
        AppContext.setProperty(AUTOIMPORT_CONFIG, notExistConfig)
        delegate.getFileDescriptors()

        then:
        thrown(FileNotFoundException.class)
    }

    def "if auto-import xml is empty"() {
        when:
        AppContext.setProperty(AUTOIMPORT_CONFIG, emptyConfig)
        List<AutoImportFileDescriptor> descriptors = delegate.getFileDescriptors()

        then:
        descriptors.size() == 0
    }

    def "check valid configuration"() {
        when:
        AppContext.setProperty(AUTOIMPORT_CONFIG, validConfig)
        List<AutoImportFileDescriptor> descriptors = delegate.getFileDescriptors()

        then:
        descriptors.size() == 2
        AutoImportFileDescriptor rolesDescriptor = descriptors.get(0)
        AutoImportFileDescriptor groupsDescriptor = descriptors.get(1)

        rolesDescriptor.getPath() == 'com/company/example/Roles.zip'
        rolesDescriptor.getBean() == 'admintools_DefaultAutoImportProcessor'

        groupsDescriptor.getPath() == 'com/company/example/Groups.json'
        groupsDescriptor.getImportClass() == 'com.company.example.SomeProcessor'
    }


}
