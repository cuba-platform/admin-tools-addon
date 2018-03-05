package com.haulmont.addon.admintools.sys

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.global.auto_import.AutoImportBuildSupport
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Resources
import org.apache.commons.lang.StringUtils
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class AutoImportBuildSupportTest extends Specification {

    @ClassRule @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImportBuildSupport delegate
    Resources resources

    void setup() {
        delegate = AppBeans.get(AutoImportBuildSupport)
        resources = Mock(Resources) {
            getResourceAsStream() >> null
        }
        delegate.resources = resources
    }

    def "checks that method getAutoImportConfig returns not empty config"() {
        expect:
        !StringUtils.isBlank(delegate.getAutoImportConfig())
    }

    def "checks that method readXml throws exception with incorrect file path"() {
        when:
        delegate.readXml(null)
        then:
        thrown(IllegalStateException)

        when:
        delegate.readXml('com/example/invalid.zip')
        then:
        thrown(IllegalStateException)
    }
}
