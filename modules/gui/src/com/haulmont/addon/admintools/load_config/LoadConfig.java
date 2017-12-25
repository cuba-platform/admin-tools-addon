package com.haulmont.addon.admintools.load_config;

import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.components.*;
import org.apache.commons.io.IOUtils;

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

    protected String targetFileName;
    protected File dir;
    protected File targetFile;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String configDir = configuration.getConfig(GlobalConfig.class).getConfDir();

        uploadField.addFileUploadSucceedListener(event -> {
            targetFileName = uploadField.getFileName();
            dir = new File(configDir);
            targetFile = new File(dir, targetFileName);
        });
    }

    public void apply() {
        if (targetFile.exists()) {
            confirmOverwriteFile(targetFileName, targetFile);
        } else {
            uploadFile(targetFile);
        }
    }

    public void cancel() {
        this.close("cancel");
    }

    protected void confirmOverwriteFile(String fileName, File targetFile) {
        showOptionDialog(
                getMessage("replaceConfirmation"),
                formatMessage(getMessage("replaceMessage"), fileName) ,
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.OK) {
                            @Override
                            public void actionPerform(Component component) {
                                uploadFile(targetFile);
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                }
        );
    }

    protected void uploadFile(File targetFile) {
        InputStream originalFile = uploadField.getFileContent();
        boolean failed = false;
        try (FileOutputStream fileOutput = new FileOutputStream(targetFile)) {
            if (originalFile != null) {
                IOUtils.copy(originalFile, fileOutput);
            } else {
                failed = true;
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