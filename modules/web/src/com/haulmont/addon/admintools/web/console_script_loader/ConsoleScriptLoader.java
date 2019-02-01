package com.haulmont.addon.admintools.web.console_script_loader;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class ConsoleScriptLoader extends AbstractWindow {

    @Inject
    protected FileUploadField uploadField;

    @Inject
    protected Notifications notifications;

    @Inject
    protected Screens screens;

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
        close(new StandardCloseAction("cancel"));
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
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(e.getLocalizedMessage())
                    .show();
        }
    }

    protected void openConsole(String windowAlias, ZipArchiveInputStream archiveReader) throws IOException {
        String script = IOUtils.toString(archiveReader, UTF_8);
        screens.create(windowAlias, OpenMode.THIS_TAB,new MapScreenOptions(singletonMap("script", script))).show();
    }

    protected boolean isFileValid() {
        FileDescriptor fileDescriptor = uploadField.getValue();

        if (fileDescriptor == null) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(getMessage("fileNotUploaded"))
                    .show();
            return false;
        }

        if (!"zip".equals(fileDescriptor.getExtension())) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(getMessage("isNotZip"))
                    .show();
            return false;
        }

        return true;
    }


}
