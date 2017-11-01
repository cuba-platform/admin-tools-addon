package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport.AutoImportObject;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authentication;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportBuildSupport buildSupport;
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
                try {
                    processAutoImportObject(importObject);
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
        } finally {
            authentication.end();
        }
    }

    public void processAutoImportObject(AutoImportObject importObject) throws Exception {

        AutoImportProcessor autoImportProcessor;
        if (importObject.bean != null) {
            log.info("Importing file {} by bean {}", importObject.path, importObject.bean);
            autoImportProcessor = AppBeans.get(importObject.bean);
            autoImportProcessor.processFile(importObject.path);
        } else if (importObject.importClass != null) {
            log.info("Importing file {} by class {}", importObject.path, importObject.importClass);
            autoImportProcessor =
                    (AutoImportProcessor) Class.forName(importObject.importClass).newInstance();
            autoImportProcessor.processFile(importObject.path);
        }

    }

    @Override
    public void applicationStopped() {
    }
}
