package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected AutoImportBuildSupport autoImportBuildSupport;
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
            List<AutoImportBuildSupport.AutoImportObject> list = autoImportBuildSupport.convertXmlToObject(autoImportBuildSupport.init());

            for (AutoImportBuildSupport.AutoImportObject importObject: list) {
                InputStream stream = resources.getResourceAsStream(importObject.path);
                if (stream == null) {
                    continue;
                }

                AutoImportProcessor autoImportProcessor = null;
                if (importObject.bean != null) {
                    autoImportProcessor = AppBeans.get(importObject.bean);
                    autoImportProcessor.processFile(stream);
                } else if (importObject.importClass != null) {
                    try {
                        autoImportProcessor = (AutoImportProcessor) Class.forName(importObject.importClass).newInstance();
                        autoImportProcessor.processFile(stream);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
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
