package com.haulmont.addon.admintools.core.auto_import;

import com.haulmont.addon.admintools.core.auto_import.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.global.auto_import.AutoImportXmlReader;
import com.haulmont.addon.admintools.global.auto_import.ImportedFilesConfig;
import com.haulmont.addon.admintools.global.auto_import.dto.AutoImportFileDescriptor;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportStatusInfo;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportedFilesInfo;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.security.app.Authenticated;
import io.vavr.control.Either;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.haulmont.addon.admintools.global.auto_import.dto.ImportStatus.FAILURE;
import static com.haulmont.addon.admintools.global.auto_import.dto.ImportStatus.SUCCESS;
import static io.vavr.API.*;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static java.lang.String.format;

/**
 * This class delegates import files to corresponding {@link AutoImportProcessor} by the descriptors,
 * which received from {@link AutoImportXmlReader}. If file was imported
 * in the previous iteration and not been changed, then it will be skipped. Information about
 * imported files stored in {@link ImportedFilesConfig}
 */
@Component(AutoImporter.NAME)
public class AutoImporterImpl implements AutoImporter {
    public static final String NONE = "none";
    public static final ImportStatusInfo IS_NOT_IMPORTED = new ImportStatusInfo(NONE, FAILURE);

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportXmlReader configReader;
    @Inject
    protected ImportedFilesConfig importedFilesConfig;
    @Inject
    protected Resources resources;

    @Authenticated
    @Override
    public void startImport() {
        try {
            importFiles();
        } catch (Exception e) {
            log.error("Import error", e);
        }
    }

    protected void importFiles() throws IOException {
        List<AutoImportFileDescriptor> filteredDescriptors = filterDescriptors(configReader.getFileDescriptors());
        ImportedFilesInfo importedObjects = importedFilesConfig.getImportedFilesInfo();

        for (AutoImportFileDescriptor fileDescriptor : filteredDescriptors) {
            importFile(importedObjects, fileDescriptor);
        }
        importedFilesConfig.setImportedFilesInfo(importedObjects);
    }

    protected void importFile(ImportedFilesInfo importedObjects, AutoImportFileDescriptor fileDescriptor) {
        String filePath = fileDescriptor.getPath();
        String newMd5Hex;

        try {
            newMd5Hex = calculateMd5Hex(filePath);
        } catch (IOException e) {
            log.error("Import error, when was calculated hash", e);
            return;
        }

        if (fileWillBeSkipped(importedObjects, filePath, newMd5Hex)) {
            log.info("File {} is skipped", filePath);
            return;
        }

        log.info("File {} is importing...", filePath);

        try {
            processAutoImportObject(fileDescriptor);
            importedObjects.put(filePath, new ImportStatusInfo(newMd5Hex, SUCCESS));
            log.info("File {} has been imported", filePath);
        } catch (Exception e) {
            importedObjects.put(filePath, new ImportStatusInfo(newMd5Hex, FAILURE));
            log.error(format("Importing file %s has been failed", filePath), e);
        }
    }

    protected List<AutoImportFileDescriptor> filterDescriptors(List<AutoImportFileDescriptor> descriptors) {
        return descriptors.stream()
                .map(this::applyValidationChecks)
                .flatMap(either -> Match(either).of(
                        Case($Right($()), descriptor -> Stream.of(descriptor)),
                        Case($Left($()), errMsg -> {
                            log.error("Import error, when was filtered file descriptors " + errMsg);
                            return Stream.empty();
                        })
                )).collect(Collectors.toList());
    }

    protected Either<String, AutoImportFileDescriptor> applyValidationChecks(AutoImportFileDescriptor autoImport) {
        if (autoImport.getBean() != null && autoImport.getImportClass() != null) {
            return Either.left("Bean and class defined");
        }

        if (autoImport.getBean() == null && autoImport.getImportClass() == null) {
            return Either.left("");
        }

        if (autoImport.getPath() == null) {
            return Either.left("");
        }

        return Either.right(autoImport);
    }

    protected boolean fileWillBeSkipped(ImportedFilesInfo previousImportsInfo, String filePath, String newMd5Hex) {
        ImportStatusInfo prevImportStatus = previousImportsInfo.getOrDefault(filePath, IS_NOT_IMPORTED);
        return Objects.equals(prevImportStatus.getHex(), newMd5Hex);
    }

    protected String calculateMd5Hex(String path) throws IOException {
        try (InputStream stream = resources.getResourceAsStream(path)) {
            if (stream == null) {
                throw new FileNotFoundException(format("File not found by the path %s", path));
            }
            return DigestUtils.md5Hex(stream);
        }
    }

    protected void processAutoImportObject(AutoImportFileDescriptor fileDescriptor) throws Exception {
        AutoImportProcessor autoImportProcessor = resolveEffectiveProcessor(fileDescriptor);
        log.info("File {} is gonna be processed by {}", fileDescriptor.getPath(), autoImportProcessor);

        autoImportProcessor.processFile(fileDescriptor.getPath());
    }

    protected AutoImportProcessor resolveEffectiveProcessor(AutoImportFileDescriptor autoImport) throws Exception {
        return Optional
                .ofNullable(autoImport.getBean()).<AutoImportProcessor>map(AppBeans::get)
                .orElseGet(() -> getInstanceByClassName(autoImport.getImportClass()));
    }

    @SuppressWarnings("RuntimeException")
    protected AutoImportProcessor getInstanceByClassName(String className) {
        try {
            return (AutoImportProcessor) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
