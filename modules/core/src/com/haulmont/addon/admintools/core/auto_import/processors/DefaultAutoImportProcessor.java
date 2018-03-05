package com.haulmont.addon.admintools.core.auto_import.processors;

import com.google.common.io.Files;
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.String.format;

@Component("admintools_DefaultAutoImportProcessor")
public class DefaultAutoImportProcessor implements AutoImportProcessor {

    @Inject
    protected EntityImportExportService entityImportExportService;
    @Inject
    protected Resources resources;
    @Inject
    protected Metadata metadata;
    @Inject
    protected EntityImportViewBuilderAPI viewBuilder;

    @Override
    public void processFile(String filePath) throws Exception {
        File file = resources.getResource(filePath).getFile();
        String fileExtension = Files.getFileExtension(filePath);
        FileType fileType = FileType.getEnum(fileExtension);

        switch (fileType) {
            case ZIP:
                processZipFile(new ZipFile(file));
                break;
            case JSON:
                processJsonFile(file);
                break;
        }
    }

    protected void processZipFile(ZipFile zipFile) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            try (InputStream fileStream = zipFile.getInputStream(entries.nextElement())) {
                processJsonStream(fileStream);
            }
        }
    }

    protected void processJsonFile(File jsonFile) throws IOException {
        try (InputStream jsonStream = new FileInputStream(jsonFile)) {
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
