package com.haulmont.addon.admintools.core.auto_import

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.global.auto_import.AutoImportXmlReader
import com.haulmont.addon.admintools.global.auto_import.ImportedFilesConfig
import com.haulmont.addon.admintools.global.auto_import.dto.AutoImportFileDescriptor
import com.haulmont.addon.admintools.global.auto_import.dto.ImportedFilesInfo
import com.haulmont.cuba.core.global.AppBeans
import org.junit.ClassRule
import org.slf4j.Logger
import spock.lang.Shared
import spock.lang.Specification

class AutoImporterImplTest extends Specification {

    @ClassRule
    @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImporterImpl autoImporter

    AutoImportXmlReader configReader
    ImportedFilesConfig importedFilesConfig
    Logger log

    void setup() {
        autoImporter = AppBeans.get(AutoImporterImpl)
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
        1 * log.error(e.localizedMessage, e)
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
