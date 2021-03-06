/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.admintools.global.script_generator;

import com.haulmont.cuba.core.entity.Entity;

import java.util.Set;

/**
 * This interface is used for generating SQL scripts
 */
public interface EntityViewSqlGenerationService {

    String NAME = "admintools_EntityViewSqlGenerationService";

    /**
     * @param entity entity to generate SQL scripts (SELECT, INSERT, etc.)
     * @param viewName view to generate SQL scripts (SELECT, INSERT, etc.)
     * @param scriptType is type of generated SQL scripts (SELECT, INSERT, etc.)
     * @return set of SQL scripts for @{@param entity} and nested entities by {@param viewName}
     */
    Set<String> generateScript(Entity entity, String viewName, ScriptGenerationOptions scriptType);
}
