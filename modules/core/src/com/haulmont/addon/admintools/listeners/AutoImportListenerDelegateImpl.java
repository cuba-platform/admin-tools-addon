package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.config.AutoImportConfiguration;
import com.haulmont.addon.admintools.dto.ImportDataObject;
import com.haulmont.addon.admintools.dto.ImportFileObjects;
import com.haulmont.addon.admintools.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.security.app.Authentication;
import io.vavr.CheckedFunction1;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
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

import static com.haulmont.addon.admintools.dto.ImportStatus.FAILURE;
import static com.haulmont.addon.admintools.dto.ImportStatus.SUCCESS;
import static io.vavr.API.*;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static java.lang.String.format;

@Component(AutoImportListenerDelegate.NAME)
public class AutoImportListenerDelegateImpl implements AutoImportListenerDelegate {
    public static final String MD5_ERROR_STONE = "md5 error";
    public static final ImportDataObject WORSE_CASE = new ImportDataObject(MD5_ERROR_STONE, FAILURE);

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportBuildSupport buildSupport;
    @Inject
    protected Authentication authentication;
    @Inject
    protected AutoImportConfiguration autoImportConfiguration;
    @Inject
    protected Resources resources;

    @SuppressWarnings("Convert2MethodRef")
    public void applicationStarted() {
        authentication.begin();
        try {
            List<AutoImportBuildSupport.AutoImportObject> autoImportObjects =
                    buildSupport.convertXmlToObject(buildSupport.retrieveImportXmlFile());
            List<AutoImportBuildSupport.AutoImportObject> filteredAutoImports = autoImportObjects.stream()
                    .map(this::applyValidationChecks)
                    .flatMap(either -> Match(either).of(
                            Case($Right($()), ai -> Stream.of(ai)),
                            Case($Left($()), errMsg -> { log.warn(errMsg); return Stream.empty(); })
                    )).collect(Collectors.toList());

            ImportFileObjects importedObjects = autoImportConfiguration.getImportFileInformation();
            for (AutoImportBuildSupport.AutoImportObject importObject : filteredAutoImports) {
                log.info("File {} is importing...", importObject.getPath());

                ImportDataObject prevIteration = importedObjects.getOrDefault(importObject.getPath(), WORSE_CASE);
                String newMd5Hex = calculateMd5Hex(importObject.getPath(), this::getResourceAsStreamNN)
                        .onFailure(cause -> log.warn(cause.getMessage(), cause))
                        .getOrElse(MD5_ERROR_STONE);

                if (willObjectSkip(importObject.getPath(), prevIteration, newMd5Hex)) {
                    log.info("File {} is skipped", importObject.getPath());
                    continue;
                }

                try {
                    processAutoImportObject(importObject);
                    importedObjects.put(importObject.getPath(), new ImportDataObject(newMd5Hex, SUCCESS));
                    log.info("File {} has been imported", importObject.getPath());
                } catch (Exception e) {
                    importedObjects.put(importObject.getPath(), new ImportDataObject(newMd5Hex, FAILURE));
                    log.warn("", e);
                    log.warn("Importing file {} has been failed", importObject.getPath());
                }
            }
            autoImportConfiguration.setImportFileInformation(importedObjects);
        } finally {
            authentication.end();
        }
    }

    protected Either<String, AutoImportBuildSupport.AutoImportObject> applyValidationChecks(AutoImportBuildSupport.AutoImportObject autoImport) {
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

    @SuppressWarnings("unused")
    protected boolean willObjectSkip(String path, ImportDataObject prevImportInteraction, String newMd5Hex) {
        return Objects.equals(prevImportInteraction.getHex(), newMd5Hex);
    }

    public Try<String> calculateMd5Hex(String path, CheckedFunction1<? super String, ? extends InputStream> mapper) {
        return Try.of(() -> DigestUtils.md5Hex(mapper.apply(path)));
    }

    protected InputStream getResourceAsStreamNN(String path) throws IOException {
        if (StringUtils.isBlank(path)) throw new IOException("File path is empty");
        InputStream is = resources.getResourceAsStream(path);

        // see documentation for {@link Resources#getResourceAsStream}
        if (is == null) throw new FileNotFoundException(format("File not found %s", path));

        return is;
    }

    protected void processAutoImportObject(AutoImportBuildSupport.AutoImportObject importObject) throws Exception {
        AutoImportProcessor autoImportProcessor = resolveEffectiveProcessor(importObject);
        log.info("File {} is gonna be processed by {}", importObject.getPath(), autoImportProcessor);

        autoImportProcessor.processFile(importObject.getPath());
    }

    protected AutoImportProcessor resolveEffectiveProcessor(AutoImportBuildSupport.AutoImportObject autoImport)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return Optional
                .ofNullable(autoImport.getBean()).<AutoImportProcessor>map(AppBeans::get)
                .orElseGet(() -> getInstanceByClassName(autoImport.getImportClass()));
    }

    @SuppressWarnings("RuntimeException")
    protected  AutoImportProcessor getInstanceByClassName(String className) {
        try {
            return (AutoImportProcessor) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void applicationStopped() {
    }

}
