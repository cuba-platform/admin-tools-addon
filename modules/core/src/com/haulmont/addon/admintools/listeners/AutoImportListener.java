package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.cuba.core.sys.AppContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportBuildSupport autoImportBuildSupport;

    public AutoImportListener() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        List<AutoImportBuildSupport.AutoImportObject> list = autoImportBuildSupport.convertXmlToObject(autoImportBuildSupport.init());
        for (AutoImportBuildSupport.AutoImportObject autoImportObject : list) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>> " + autoImportObject);
        }
    }

    @Override
    public void applicationStopped() {
    }
}
