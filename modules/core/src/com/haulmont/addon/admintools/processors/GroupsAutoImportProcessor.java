package com.haulmont.addon.admintools.processors;

import com.haulmont.addon.admintools.exception.AutoImportException;
import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportExportService;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.security.entity.Constraint;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.GroupHierarchy;
import com.haulmont.cuba.security.entity.SessionAttribute;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component("autoimport_GroupsAutoImportProcessor")
public class GroupsAutoImportProcessor implements AutoImportProcessor {

    protected EntityImportExportService entityImportExportService;
    protected Resources resources;
    protected Logger log = LoggerFactory.getLogger(GroupsAutoImportProcessor.class);

    public GroupsAutoImportProcessor(EntityImportExportService entityImportExportService, Resources resources) {
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
        processFile(stream);
    }

    @Override
    public void processFile(InputStream inputStream) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            entityImportExportService.importEntitiesFromZIP(fileBytes, createGroupsImportView());
        } catch (IOException e) {
            throw new AutoImportException("Unable to import Groups file", e);
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
