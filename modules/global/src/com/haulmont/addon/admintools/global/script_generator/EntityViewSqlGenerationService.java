package com.haulmont.addon.admintools.global.script_generator;

import com.haulmont.cuba.core.entity.Entity;

import java.util.Set;

/**
 * This interface is used for generating SQL scripts
 */
public interface EntityViewSqlGenerationService {

    String NAME = "admintools_EntityViewSqlGenerationService";

    /**
     * @param scriptType is type of generated SQL scripts (SELECT, INSERT, etc.)
     * @return set of SQL scripts for @{@code entity} and nested entities by {@code viewName}
     */
    Set<String> generateScript(Entity entity, String viewName, ScriptGenerationOptions scriptType);
}
