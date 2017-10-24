package com.haulmont.addon.admintools.processors;

import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportExportService;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.Constraint;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.GroupHierarchy;
import com.haulmont.cuba.security.entity.SessionAttribute;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class GroupsAutoImportProcessor implements AutoImportProcessor {

    protected EntityImportExportService entityImportExportService = AppBeans.get(EntityImportExportService.class);

    @Override
    public void processFile(String filePath) {
    }

    @Override
    public void processFile(InputStream inputStream) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            entityImportExportService.importEntitiesFromZIP(fileBytes, createGroupsImportView());
        } catch (IOException e) {
            throw new RuntimeException("Unable to import Groups file", e);
        }
    }

    protected EntityImportView createGroupsImportView() {
        return new EntityImportView(Group.class)
                .addLocalProperties()
                .addManyToOneProperty("parent", ReferenceImportBehaviour.ERROR_ON_MISSING)
                .addOneToManyProperty("hierarchyList",
                        new EntityImportView(GroupHierarchy.class)
                                .addLocalProperties()
                                .addManyToOneProperty("parent", ReferenceImportBehaviour.ERROR_ON_MISSING),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS)
                .addOneToManyProperty("sessionAttributes",
                        new EntityImportView(SessionAttribute.class).addLocalProperties(),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS)
                .addOneToManyProperty("constraints",
                        new EntityImportView(Constraint.class).addLocalProperties(),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS);
    }
}
