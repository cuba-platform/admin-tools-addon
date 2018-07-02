package com.haulmont.addon.admintools.core.auto_import;

/**
 * This interface should be implemented by any class whose imports files,
 * when application is started.
 * See {@link  com.haulmont.addon.admintools.core.AdminToolsCoreStarter}
 */
public interface AutoImporter {

    String NAME = "admintools_AutoImporter";

    void startImport();
}
