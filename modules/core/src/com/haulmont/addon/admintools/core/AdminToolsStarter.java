package com.haulmont.addon.admintools.core;

import com.haulmont.addon.admintools.core.auto_import.AutoImporter;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.events.AppContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * This class runs inner components, when the application is started
 */
@Component("admintools_AdminToolsStarter")
public class AdminToolsStarter {

    @Inject
    protected AutoImporter autoImporter;

    @EventListener
    public void applicationStarted(AppContextStartedEvent event) {
        autoImporter.startImport();
    }
}
