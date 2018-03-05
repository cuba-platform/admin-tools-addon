package com.haulmont.addon.admintools.global.auto_import;

import com.haulmont.addon.admintools.global.auto_import.type.AutoImportType;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportFileObjects;
import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.Stringify;

@Source(type = SourceType.DATABASE)
public interface AutoImportConfiguration extends Config {

    @Property("admintools.hashes")
    @Factory(factory = AutoImportType.Factory.class)
    @Stringify(stringify = AutoImportType.Stringify.class)
    @Default("{}")
    ImportFileObjects getImportFileInformation();

    void setImportFileInformation(ImportFileObjects hashes);
}
