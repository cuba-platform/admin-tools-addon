package com.haulmont.addon.admintools.processors;

import com.haulmont.addon.admintools.exception.AutoImportException;
import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportExportService;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.security.entity.Permission;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component("autoimport_RolesAutoImportProcessor")
public class RolesAutoImportProcessor implements AutoImportProcessor {

    protected EntityImportExportService entityImportExportService;
    protected Resources resources;
    protected Logger log = LoggerFactory.getLogger(RolesAutoImportProcessor.class);

    private String filePath;

    public RolesAutoImportProcessor(EntityImportExportService entityImportExportService, Resources resources) {
        this.entityImportExportService = entityImportExportService;
        this.resources = resources;
    }

    @Override
    public void processFile(String filePath) {
        InputStream stream = resources.getResourceAsStream(filePath);
        if (stream == null) {
            log.warn("File {} not found.", filePath);
            return;
        }
        this.filePath = filePath;
        processFile(stream);
    }

    @Override
    public void processFile(InputStream inputStream) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            entityImportExportService.importEntitiesFromZIP(fileBytes, createRolesImportView());
        } catch (IOException | RuntimeException e) {
            throw new AutoImportException("Unable to import Roles file", e);
        }
    }

    protected EntityImportView createRolesImportView() {
        return new EntityImportView(Role.class)
                .addLocalProperties()
                .addOneToManyProperty("permissions",
                        new EntityImportView(Permission.class).addLocalProperties(),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS);
    }
}
