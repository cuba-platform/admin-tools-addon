package com.haulmont.addon.admintools.gui.load_config;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang.StringUtils.*;

public class LoadConfig extends AbstractWindow {

    @Inject
    protected Configuration configuration;
    @Inject
    protected FileUploadField uploadField;
    @Inject
    protected TextField configPathField;
    @Inject
    protected Messages messages;

    protected Path webConfigDir;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        webConfigDir = Paths.get(configuration.getConfig(GlobalConfig.class).getConfDir());
    }

    public void apply() {
        FileDescriptor fileDescriptor = uploadField.getValue();

        if (fileDescriptor == null) {
            showNotification(getMessage("fileNotUploaded"));
        } else {
            String pathFieldValue = parse(configPathField.getRawValue().trim());

            try {
                Path filePath = webConfigDir.resolve(pathFieldValue);
                String fileName = fileDescriptor.getName();
                File targetFile = new File(filePath.toString(), fileName);

                if (targetFile.exists()) {
                    confirmOverwriteFile(fileName, targetFile);
                } else {
                    createNewFile(targetFile);
                }
            } catch (InvalidPathException e) {
                showNotification(formatMessage(getMessage("pathValidMessage"), pathFieldValue));
            }
        }
    }

    public void cancel() {
        this.close("cancel");
    }

    protected void confirmOverwriteFile(String fileName, File targetFile) {
        showOptionDialog(
                getMessage("replaceConfirmation"),
                formatMessage(getMessage("replaceMessage"), fileName),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK) {
                            @Override
                            public void actionPerform(Component component) {
                                writeToFile(targetFile);
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                }
        );
    }

    protected void createNewFile(File targetFile) {
        targetFile.getParentFile().mkdirs();
        writeToFile(targetFile);
    }

    protected void writeToFile(File targetFile) {
        InputStream loadedFile = uploadField.getFileContent();

        if (loadedFile == null) {
            showNotification(getMessage("uploadFailed"), NotificationType.ERROR);
            return;
        }

        try {
            Files.copy(loadedFile, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showNotification(getMessage("uploadSuccessful"), NotificationType.HUMANIZED);
            uploadField.setValue(null);

            if ("properties".equals(getExtension(targetFile.getPath()))) {
                messages.clearCache();
            }
        } catch (IOException e) {
            showNotification(getMessage("uploadFailed"), NotificationType.ERROR);
        }
    }

    protected String parse(String fieldPath) {
        char[] separators = new char[]{'\\', '/'};
        //replace all '.' to '/' if path not contains separators
        if (contains(fieldPath, '.') && !containsAny(fieldPath, separators)) {
            return replaceChars(fieldPath, '.', '/');
            //if first character is separator then exclude it
        } else if (isNotBlank(fieldPath) && containsAny(valueOf(fieldPath.charAt(0)), separators)) {
            return fieldPath.substring(1);
        }
        return fieldPath;
    }
}