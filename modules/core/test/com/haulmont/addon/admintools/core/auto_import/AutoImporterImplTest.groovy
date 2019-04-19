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
import com.haulmont.addon.admintools.global.auto_import.ImportedFilesConfig
import com.haulmont.addon.admintools.global.auto_import.dto.AutoImportFileDescriptor
import com.haulmont.addon.admintools.global.auto_import.dto.ImportedFilesInfo
import org.junit.ClassRule
import org.slf4j.Logger
import spock.lang.Shared
import spock.lang.Specification

class AutoImporterImplTest extends Specification {

    @ClassRule
    @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImporter autoImporter

    AutoImportXmlReader configReader
    ImportedFilesConfig importedFilesConfig
    Logger log

    void setup() {
        autoImporter = new AutoImporterImpl()
        configReader = Mock()
        importedFilesConfig = Mock()
        log = Mock()

        autoImporter.configReader = configReader
        autoImporter.importedFilesConfig = importedFilesConfig
        autoImporter.log = log

        importedFilesConfig.getImportedFilesInfo() >> new ImportedFilesInfo()
        importedFilesConfig.setImportedFilesInfo(_) >> null
    }

    def "logging error if AutoImportXmlReader thrown Exception"() {
        setup:
        Exception e = new Exception('config reader exception')
        configReader.getFileDescriptors() >> { throw e }

        when:
        autoImporter.startImport()

        then:
        1 * log.error("Import error", e)
    }

    def "logging warning if path not exist"() {
        setup:
        String notExistPath = 'not/exist'
        configReader.getFileDescriptors() >> [
                new AutoImportFileDescriptor(notExistPath, '', null)
        ]

        when:
        autoImporter.startImport()

        then:
        log.warn(_,_) >> {message, exc ->
            assert message == 'File not found by the path not/exist'
            assert exc instanceof FileNotFoundException
        }
    }
}
