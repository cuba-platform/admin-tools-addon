package com.haulmont.addon.admintools.core.auto_import.listeners

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.global.auto_import.dto.ImportDataObject
import com.haulmont.addon.admintools.global.auto_import.AutoImportBuildSupport
import com.haulmont.cuba.core.global.AppBeans
import org.junit.ClassRule
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import spock.lang.Shared
import spock.lang.Specification

class AutoImportListenerDelegateImplTest extends Specification {

    @ClassRule @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImportListenerDelegateImpl delegate

    @Shared
    ImportDataObject importDataObject

    void setupSpec() {
        importDataObject = new ImportDataObject('a001', null)
    }

    void setup() {
        delegate = AppBeans.get(AutoImportListenerDelegateImpl)
    }

    def "checks that method willObjectSkip compares hashes correctly"() {
        expect:
        delegate.willObjectSkip(path, prevImportInteraction, newMd5Hex) == res

        where:
        path | prevImportInteraction | newMd5Hex | res
        ''   | importDataObject      | 'a001'    | true
        ''   | importDataObject      | 'a002'    | false
    }

    def "checks that method getResourceAsStreamNN throws exception with incorrect file path"() {
        when:
        delegate.getResourceAsStreamNN(null)
        then:
        thrown(IOException)

        when:
        delegate.getResourceAsStreamNN('com/example/invalid.zip')
        then:
        thrown(FileNotFoundException)
    }

    def "checks that method resolveEffectiveProcessor throws exception with incorrect class or bean"() {
        when:
        delegate.resolveEffectiveProcessor(
                new AutoImportBuildSupport.AutoImportObject('', 'admintools_InvalidBeanName', ''))
        then:
        thrown(NoSuchBeanDefinitionException)

        when:
        delegate.resolveEffectiveProcessor(
                new AutoImportBuildSupport.AutoImportObject('', null, 'com.test.example.InvalidProcessor'))
        then:
        thrown(RuntimeException)

        when:
        delegate.resolveEffectiveProcessor(
                new AutoImportBuildSupport.AutoImportObject('', null, null))
        then:
        thrown(RuntimeException)
    }


}
