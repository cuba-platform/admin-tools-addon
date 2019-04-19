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

package com.haulmont.addon.admintools.core.auto_import.processors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.addon.admintools.core.auto_import.AutoImportException;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.app.importexport.EntityImportExportService;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.EntityImportViewBuilderAPI;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Resources;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;

import static com.google.common.io.Files.getFileExtension;
import static java.lang.String.format;

/**
 * This class imports Entities from a json file or a zip with json files and
 * adds them to a database. The order of json files in zip is random.
 */
@Component("admintools_DefaultAutoImportProcessor")
public class DefaultAutoImportProcessor implements AutoImportProcessor {

    @Inject
    protected EntityImportExportService entityImportExportService;
    @Inject
    protected Resources resources;
    @Inject
    protected Metadata metadata;
    @Inject
    @Qualifier("admintools_ExtendedEntityImportViewBuilder")
    protected EntityImportViewBuilderAPI viewBuilder;

    /**
     * @param filePath is a classpath to a file
     * @throws Exception
     */
    @Override
    public void processFile(String filePath) throws Exception {
        FileType fileType = FileType.getEnum(getFileExtension(filePath));
        if (!resources.getResource(filePath).exists()) {
            throw new FileNotFoundException(filePath);
        }
        switch (fileType) {
            case ZIP:
                processZipFile(filePath);
                break;
            case JSON:
                processJsonFile(filePath);
                break;
        }
    }

    protected void processZipFile(String zipFile) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(resources.getResourceAsStream(zipFile));
        ZipArchiveInputStream is = new ZipArchiveInputStream(bis);

        try {
            ZipArchiveEntry entry;
            while ((entry = is.getNextZipEntry()) != null) {
                final byte[] buffer = IOUtils.toByteArray(is);
                processJsonStream(new ByteArrayInputStream(buffer));
            }
        } finally {
            is.close();
        }
    }

    protected void processJsonFile(String jsonFile) throws IOException {
        try (InputStream jsonStream = resources.getResourceAsStream(jsonFile)) {
            processJsonStream(jsonStream);
        }
    }

    protected void processJsonStream(InputStream jsonStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream))) {
            JsonArray objects = new JsonParser().parse(reader).getAsJsonArray();

            for (JsonElement jsonElement : objects) {
                importObject(jsonElement.getAsJsonObject());
            }
        }
    }

    protected void importObject(JsonObject object) {
        String entityName = object.get("_entityName").getAsString();
        String objectJson = object.toString();
        MetaClass metaClass = metadata.getSession().getClassNN(entityName);
        EntityImportView entityImportView = viewBuilder.buildFromJson(objectJson, metaClass);

        entityImportExportService.importEntitiesFromJSON("[" + objectJson + "]", entityImportView);
    }

    public enum FileType {
        JSON("json"), ZIP("zip");

        protected final String extension;

        FileType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static FileType getEnum(String value) {
            for (FileType v : values()) {
                if (v.getExtension().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new AutoImportException(format("File type is not supported: %s", value));
        }
    }
}
