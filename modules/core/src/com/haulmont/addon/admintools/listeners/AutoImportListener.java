package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport.AutoImportObject;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authentication;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportBuildSupport buildSupport;
    @Inject
    protected Resources resources;
    @Inject
    protected Authentication authentication;

    public AutoImportListener() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        authentication.begin();
        try {
            List<AutoImportObject> list = buildSupport.convertXmlToObject(buildSupport.retrieveImportXmlFile());

            for (AutoImportObject importObject: list) {
                InputStream stream = resources.getResourceAsStream(importObject.path);
                if (stream == null) {
                    log.debug("File " + importObject.path + " not found.");
                    continue;
                }

                AutoImportProcessor autoImportProcessor;
                if (importObject.bean != null) {
                    autoImportProcessor = AppBeans.get(importObject.bean);
                    autoImportProcessor.processFile(stream);
                } else if (importObject.importClass != null) {
                    try {
                        autoImportProcessor =
                                (AutoImportProcessor) Class.forName(importObject.importClass).newInstance();
                        autoImportProcessor.processFile(stream);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        log.debug(e.getMessage());
                    }
                }

            }
        } finally {
            authentication.end();
        }
    }

    @Override
    public void applicationStopped() {
    }
}
