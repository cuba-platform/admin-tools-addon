package com.haulmont.addon.admintools.processors

import com.haulmont.addon.admintools.exception.AutoImportException
import spock.lang.Specification

class RolesAutoImportProcessorTest extends Specification {

    AutoImportProcessor delegate
    InputStream inputStream

    void setup() {
        delegate = new RolesAutoImportProcessor()
        inputStream = new ByteArrayInputStream(new byte[0])

    }

    def "checks that method processFile throws exception with invalid Roles file"() {
        when:
        delegate.processFile(inputStream)
        then:
        thrown(AutoImportException)
    }
}
