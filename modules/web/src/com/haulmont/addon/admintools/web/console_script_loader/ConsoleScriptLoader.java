package com.haulmont.addon.admintools.web.console_script_loader;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FileUploadField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.ERROR;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class ConsoleScriptLoader extends AbstractWindow {

    @Inject
    protected FileUploadField uploadField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void apply() {
        if (!isFileValid()) {
            return;
        }

        readZip();
    }

    public void cancel() {
        this.close("cancel");
    }

    protected void readZip() {
        try (InputStream is = uploadField.getFileContent();
             ZipArchiveInputStream archiveReader = new ZipArchiveInputStream(is)) {

            ZipArchiveEntry entry;
            while ((entry = archiveReader.getNextZipEntry()) != null) {
                switch (getExtension(entry.getName())) {
                    case "groovy":
                        openConsole("groovyConsole", archiveReader);
                        break;
                    case "jpql":
                        openConsole("jpqlConsole", archiveReader);
                        break;
                    case "sql":
                        openConsole("sqlConsole", archiveReader);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            showNotification(e.getLocalizedMessage(), ERROR);
        }
    }

    protected void openConsole(String windowAlias, ZipArchiveInputStream archiveReader) throws IOException {
        String script = IOUtils.toString(archiveReader, UTF_8);
        openWindow(windowAlias, WindowManager.OpenType.THIS_TAB, singletonMap("script", script));
    }

    protected boolean isFileValid() {
        FileDescriptor fileDescriptor = uploadField.getValue();

        if (fileDescriptor == null) {
            showNotification(getMessage("fileNotUploaded"), WARNING);
            return false;
        }

        if (!"zip".equals(fileDescriptor.getExtension())) {
            showNotification(getMessage("isNotZip"), WARNING);
            return false;
        }

        return true;
    }


}
