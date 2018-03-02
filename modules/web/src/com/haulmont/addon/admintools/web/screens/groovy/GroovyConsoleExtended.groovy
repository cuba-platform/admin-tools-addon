package com.haulmont.addon.admintools.web.screens.groovy

import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionFactory
import de.diedavids.cuba.runtimediagnose.web.screens.diagnose.DiagnoseFileDownloader
import de.diedavids.cuba.runtimediagnose.web.screens.groovy.GroovyConsole

import javax.inject.Inject

class GroovyConsoleExtended extends GroovyConsole {
    @Inject
    DiagnoseExecutionFactory diagnoseExecutionFactory
    @Inject
    DiagnoseFileDownloader diagnoseFileDownloader

    @Override
    void downloadConsoleResult() {
        def zipBytes = diagnoseExecutionFactory.createExecutionResultFromDiagnoseExecution(diagnoseExecution)
        diagnoseFileDownloader.downloadFile(this, zipBytes)
    }
}
