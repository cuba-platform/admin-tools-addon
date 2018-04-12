package com.haulmont.addon.admintools.core.auto_import;

import com.haulmont.addon.admintools.core.AdminToolsStarter;

/**
 * This interface should be implemented by any class whose imports files,
 * when application is started.
 * See {@link  AdminToolsStarter}
 */
public interface AutoImporter {

    String NAME = "admintools_AutoImporter";

    void startImport();
}
