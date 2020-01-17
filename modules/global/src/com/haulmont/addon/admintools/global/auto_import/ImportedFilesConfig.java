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

package com.haulmont.addon.admintools.global.auto_import;

import com.haulmont.addon.admintools.global.auto_import.type.AutoImportType;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportedFilesInfo;
import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.Stringify;

/**
 * This configuration contains json with file name, file hash and status of importing.
 * See com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl
 */
@Source(type = SourceType.DATABASE)
public interface ImportedFilesConfig extends Config {

    /**
     * @return information about files imported by AutoImporterImpl
     */
    @Property("admintools.importedFiles")
    @Factory(factory = AutoImportType.Factory.class)
    @Stringify(stringify = AutoImportType.Stringify.class)
    @Default("{}")
    ImportedFilesInfo getImportedFilesInfo();

    /**
     * @param filesInfo the map of imported files
     * set information about files {@param filesInfo} imported by AutoImporterImpl
     */
    void setImportedFilesInfo(ImportedFilesInfo filesInfo);
}
