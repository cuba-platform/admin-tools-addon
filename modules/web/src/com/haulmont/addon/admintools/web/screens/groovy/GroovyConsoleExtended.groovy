package com.haulmont.addon.admintools.web.screens.groovy

import com.haulmont.cuba.gui.components.FileUploadField
import com.haulmont.cuba.gui.components.SourceCodeEditor
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionFactory
import de.diedavids.cuba.runtimediagnose.web.screens.diagnose.DiagnoseFileDownloader
import de.diedavids.cuba.runtimediagnose.web.screens.groovy.GroovyConsole
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream

import javax.inject.Inject

import static java.nio.charset.StandardCharsets.UTF_8
import static org.apache.commons.io.IOUtils.closeQuietly
import static org.apache.commons.io.IOUtils.toString

/**
 * {@code GroovyConsoleExtended} overrides class {@link GroovyConsole}. There is fixed bug with a download zip,
 * if console result contains 'null'
 */
class GroovyConsoleExtended extends GroovyConsole {
    @Inject
    protected DiagnoseExecutionFactory diagnoseExecutionFactory
    @Inject
    protected DiagnoseFileDownloader diagnoseFileDownloader
    @Inject
    protected FileUploadField uploadField
    @Inject
    protected SourceCodeEditor console

    @Override
    void init(Map<String, Object> params) {
        super.init(params)
        uploadField.addFileUploadSucceedListener({ e -> setUploadListener() })
    }

    @Override
    void downloadConsoleResult() {
        def zipBytes = diagnoseExecutionFactory.createExecutionResultFromDiagnoseExecution(diagnoseExecution)
        diagnoseFileDownloader.downloadFile(this, zipBytes)
    }

    protected void setUploadListener() {
        def extension = uploadField.fileDescriptor.extension

        if ("zip" == extension) {
            String groovyScript = getScriptFromZip(uploadField.getFileContent())
            console.setValue("")
            console.setValue(groovyScript)
        } else {
            showNotification(getMessage("extensionError"))
        }
    }

    protected String getScriptFromZip(InputStream inputStream) {
        ZipArchiveInputStream archiveReader = new ZipArchiveInputStream(inputStream)

        try {
            ZipArchiveEntry entry
            while ((entry = archiveReader.getNextZipEntry()) != null &&
                    entry.getName().endsWith(".groovy")) {

                return toString(archiveReader, UTF_8)
            }
        }
        finally {
            closeQuietly(archiveReader)
        }

    }
}
