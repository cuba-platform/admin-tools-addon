package com.haulmont.addon.admintools.web.screens.console

import com.haulmont.cuba.gui.WindowParam
import com.haulmont.cuba.gui.components.FileUploadField
import com.haulmont.cuba.gui.components.SourceCodeEditor
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleFrame
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream

import javax.inject.Inject

import static com.haulmont.cuba.gui.components.Frame.NotificationType.ERROR
import static de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType.JPQL
import static de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType.SQL
import static java.nio.charset.StandardCharsets.UTF_8
import static org.apache.commons.io.FilenameUtils.getExtension
import static org.apache.commons.io.IOUtils.closeQuietly
import static org.apache.commons.io.IOUtils.toString
import static org.apache.commons.lang.StringUtils.isNotBlank

class ConsoleFrameExtended extends ConsoleFrame {
    @Inject
    SourceCodeEditor console
    @Inject
    FileUploadField uploadField
    @WindowParam(name = 'diagnoseType')
    protected DiagnoseType diagnoseType

    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        console.setValue(params.getOrDefault('script', ''))
        this.setHeightFull()
        this.setWidthFull()
        uploadField.addFileUploadSucceedListener({ e -> uploadFile() })
    }

    protected void uploadFile() {
        def extension = uploadField.fileDescriptor.extension

        if ("zip" != extension) {
            showNotification(getMessage("extensionError"))
            return
        }

        InputStream fileContent = uploadField.getFileContent()

        try {
            String script = getScriptFromZip(fileContent)
            if (isNotBlank(script)) {
                console.setValue(script)
            }
        } catch (Exception e) {
            showNotification(e.getLocalizedMessage(), ERROR)
        } finally {
            closeQuietly(fileContent)
        }
    }

    protected String getScriptFromZip(InputStream inputStream) {
        ZipArchiveInputStream archiveReader = new ZipArchiveInputStream(inputStream)

        try {
            ZipArchiveEntry entry
            while ((entry = archiveReader.getNextZipEntry()) != null) {
                String extension = getExtension(entry.getName())

                if ('jpql' == extension && diagnoseType == JPQL ||
                        'sql' == extension && diagnoseType == SQL) {

                    return toString(archiveReader, UTF_8)
                }
            }

            return ''
        }
        finally {
            closeQuietly(archiveReader)
        }
    }

}
