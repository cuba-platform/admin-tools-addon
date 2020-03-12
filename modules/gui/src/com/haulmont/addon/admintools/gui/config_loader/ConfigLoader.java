/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.admintools.gui.config_loader;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.StandardCloseAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.*;

public class ConfigLoader extends AbstractWindow {

    @Inject
    protected Configuration configuration;
    @Inject
    protected FileUploadField uploadField;
    @Inject
    protected TextField configPathField;
    @Inject
    protected Messages messages;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Dialogs dialogs;
    @Inject
    private Label<String> helpLabel;

    protected Path configDir;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        configDir = Paths.get(configuration.getConfig(GlobalConfig.class).getConfDir());
        helpLabel.setValue(String.format(getMessage("helpMessage"), configDir.normalize()));
    }

    public void apply() {
        FileDescriptor fileDescriptor = uploadField.getValue();

        if (fileDescriptor == null) {
            notifications.create()
                    .withCaption(getMessage("fileNotUploaded"))
                    .show();
            return;
        }

        String pathFieldValue = parse(configPathField.getRawValue().trim());

        try {
            Path filePath = configDir.resolve(pathFieldValue);
            String fileName = fileDescriptor.getName();
            File targetFile = new File(filePath.toString(), fileName);

            if (targetFile.exists()) {
                confirmOverwriteFile(fileName, targetFile);
            } else {
                createNewFile(targetFile);
            }
        } catch (InvalidPathException e) {
            notifications.create()
                    .withCaption(formatMessage(getMessage("pathValidMessage"), pathFieldValue))
                    .show();
        }

    }

    public void cancel() {
        close(new StandardCloseAction("cancel"));
    }

    protected void confirmOverwriteFile(String fileName, File targetFile) {
        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                .withCaption(getMessage("replaceConfirmation"))
                .withMessage(formatMessage(getMessage("replaceMessage"), fileName))
                .withActions(
                        new DialogAction(DialogAction.Type.OK) {
                            @Override
                            public void actionPerform(Component component) {
                                writeToFile(targetFile);
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL))
                .show();
    }

    protected void createNewFile(File targetFile) {
        targetFile.getParentFile().mkdirs();
        writeToFile(targetFile);
    }

    protected void writeToFile(File targetFile) {
        InputStream loadedFile = uploadField.getFileContent();

        if (loadedFile == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(getMessage("fileIsEmpty"))
                    .show();
            return;
        }

        try {
            Files.copy(loadedFile, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(getMessage("uploadSuccessful"))
                    .show();
            uploadField.setValue(null);

            if ("properties".equals(getExtension(targetFile.getPath()))) {
                messages.clearCache();
            }
        } catch (IOException e) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(e.getLocalizedMessage())
                    .show();
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