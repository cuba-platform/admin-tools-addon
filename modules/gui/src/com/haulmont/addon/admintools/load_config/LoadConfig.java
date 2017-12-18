package com.haulmont.addon.admintools.load_config;

import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

public class LoadConfig extends AbstractWindow {

    @Inject
    protected Configuration configuration;

    @Inject
    protected FileUploadField uploadField;

    protected static final int BUFFER_SIZE = 64 * 1024;

    protected String fileName;
    protected File dir;
    protected File file;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String configDir = configuration.getConfig(GlobalConfig.class).getConfDir();

        uploadField.addFileUploadSucceedListener(event -> {
            fileName = uploadField.getFileName();
            dir = new File(configDir);
            file = new File(dir, fileName);
        });
    }

    public void apply() {
        if (file.exists()) {
            confirmOverwriteFile(fileName, file);
        } else {
            uploadFile(file);
        }
    }

    public void cancel() {
        this.close("cancel");
    }

    protected void confirmOverwriteFile(String fileName, File file) {
        showOptionDialog(
                getMessage("replaceConfirmation"),
                formatMessage(getMessage("replaceMessage"), fileName) ,
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.OK) {
                            @Override
                            public void actionPerform(Component component) {
                                uploadFile(file);
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                }
        );
    }

    protected void uploadFile(File file) {
        InputStream originalFile = uploadField.getFileContent();
        boolean failed = false;
        try (FileOutputStream fileOutput = new FileOutputStream(file)) {
            byte buffer[] = new byte[BUFFER_SIZE];
            int bytesRead;
            if (originalFile != null) {
                while ((bytesRead = originalFile.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception ex) {
            failed = true;
        } finally {
            if (failed) {
                showNotification(getMessage("uploadFailed"), NotificationType.ERROR);
            } else {
                showNotification(getMessage("uploadSuccessful"), NotificationType.HUMANIZED);
                uploadField.setValue(null);
            }
        }
    }
}