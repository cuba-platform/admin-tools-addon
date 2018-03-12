package com.haulmont.addon.admintools.core.auto_import;

import com.haulmont.addon.admintools.core.AdminToolsStopStartListener;

/**
 * This interface should be implemented by any class whose imports files,
 * when application is started.
 * See {@link AdminToolsStopStartListener}
 */
public interface AutoImporter {

    String NAME = "admintools_AutoImporter";

    void startImport();
}
