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
 * This configuration
 * See com.haulmont.addon.admintools.core.auto_import.listeners.AutoImportListenerDelegate
 */
@Source(type = SourceType.DATABASE)
public interface ImportedFilesConfig extends Config {

    /**
     * @return information about files imported by AutoImportListenerDelegate
     */
    @Property("admintools.imported_files")
    @Factory(factory = AutoImportType.Factory.class)
    @Stringify(stringify = AutoImportType.Stringify.class)
    @Default("{}")
    ImportedFilesInfo getImportedFilesInfo();

    /**
     * set information about files {@param filesInfo} imported by AutoImportListenerDelegate
     */
    void setImportedFilesInfo(ImportedFilesInfo filesInfo);
}
