package com.haulmont.addon.admintools.processors;

import com.haulmont.addon.admintools.exception.AutoImportException;
import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportExportService;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.Permission;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class RolesAutoImportProcessor implements AutoImportProcessor {

    protected EntityImportExportService entityImportExportService = AppBeans.get(EntityImportExportService.class);

    @Override
    public void processFile(String filePath) {

    }

    @Override
    public void processFile(InputStream inputStream) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            entityImportExportService.importEntitiesFromZIP(fileBytes, createRolesImportView());
        } catch (IOException e) {
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
