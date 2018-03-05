package com.haulmont.addon.admintools.app;

import com.haulmont.cuba.core.entity.Entity;

import java.util.Set;

public interface EntityViewSqlGenerationService {

    String NAME = "admintools_EntityViewSqlGenerationService";

    Set<String> generateScript(Entity entity, String viewName, ScriptGenerationOptions scriptType);
}
