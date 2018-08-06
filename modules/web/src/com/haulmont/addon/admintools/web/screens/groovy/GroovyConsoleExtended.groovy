package com.haulmont.addon.admintools.web.screens.groovy

import com.haulmont.cuba.gui.components.FileUploadField
import com.haulmont.cuba.gui.components.HBoxLayout
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.components.VBoxLayout
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
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
    protected VBoxLayout consoleVBox
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected SourceCodeEditor console

    protected FileUploadField uploadField

    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        uploadField = componentsFactory.createComponent(FileUploadField.class)
        uploadField.mode = FileUploadField.FileStoragePutMode.MANUAL
        uploadField.setUploadButtonIcon('icons/upload.png')
        uploadField.setUploadButtonCaption(getMessage('uploadDiagnoseRequestFile'))
        uploadField.addFileUploadSucceedListener({ e -> uploadFile() })

        HBoxLayout hbox1 = consoleVBox.getComponentNN(0)
        HBoxLayout hbox2 = hbox1.getComponentNN(0)
        hbox2.setSpacing(true)
        hbox2.add(uploadField)

        console.setValue(params.getOrDefault('script', ''))
    }

    @Override
    void downloadConsoleResult() {
        def zipBytes = diagnoseExecutionFactory.createExecutionResultFromDiagnoseExecution(diagnoseExecution)
        diagnoseFileDownloader.downloadFile(this, zipBytes)
    }

    protected void uploadFile() {
        def extension = uploadField.fileDescriptor.extension

        if ("zip" != extension) {
            showNotification(getMessage("extensionError"))
            return
        }

        InputStream fileContent = uploadField.getFileContent()
        try {
            String groovyScript = getScriptFromZip(fileContent)
            console.setValue(groovyScript)
        } finally {
            closeQuietly(fileContent)
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
