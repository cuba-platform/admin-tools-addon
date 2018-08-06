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
     * set information about files {@param filesInfo} imported by AutoImporterImpl
     */
    void setImportedFilesInfo(ImportedFilesInfo filesInfo);
}
