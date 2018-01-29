package com.haulmont.addon.admintools.app;

import com.haulmont.cuba.core.entity.Entity;

import java.util.Set;

public interface EntityViewSqlGenerationService {

    String NAME = "admintools_EntityViewSqlGenerationService";

    Set<String> generateInsertScript(Entity entity, String viewName);

    Set<String> generateUpdateScript(Entity entity, String viewName);

    Set<String> generateSelectScript(Entity entity, String viewName);
}
