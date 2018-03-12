package com.haulmont.addon.admintools.core;

import com.haulmont.addon.admintools.core.auto_import.AutoImporter;
import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("admintools_AdminToolsStopStartListener")
public class AdminToolsStopStartListener implements AppContext.Listener {

    @Inject
    protected AutoImporter autoImporter;

    public AdminToolsStopStartListener() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        autoImporter.startImport();
    }

    @Override
    public void applicationStopped() {
    }


}
