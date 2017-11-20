package com.haulmont.addon.admintools.listeners;

import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected AutoImportListenerDelegate autoImportListenerDelegate;

    public AutoImportListener() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        autoImportListenerDelegate.applicationStarted();
    }

    @Override
    public void applicationStopped() {
    }


}
