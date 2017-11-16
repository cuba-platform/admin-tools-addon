package com.haulmont.addon.admintools.listeners;

import com.haulmont.addon.admintools.config.AutoImportConfiguration;
import com.haulmont.addon.admintools.exception.AutoImportException;
import com.haulmont.addon.admintools.processors.AutoImportProcessor;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport;
import com.haulmont.addon.admintools.sys.AutoImportBuildSupport.AutoImportObject;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authentication;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("autoimport_AutoImportListener")
public class AutoImportListener implements AppContext.Listener {

    @Inject
    protected Logger log;
    @Inject
    protected AutoImportBuildSupport buildSupport;
    @Inject
    protected Authentication authentication;
    @Inject
    protected AutoImportConfiguration autoImportConfiguration;
    @Inject
    protected Resources resources;

    public AutoImportListener() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        authentication.begin();
        try {
            autoImportConfiguration.setHashes(null);
            List<AutoImportObject> list = buildSupport.convertXmlToObject(buildSupport.retrieveImportXmlFile());
            for (AutoImportObject importObject: list) {
                String emptyFileBytesMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(new byte[0]);
                String md5 = getHashByPath(importObject.path);

                if (emptyFileBytesMd5.equals(md5)) {
                    continue;
                }
                String fileName = importObject.path.substring(importObject.path.lastIndexOf("/") + 1);
                Map<String, Map<String, Boolean>> map = autoImportConfiguration.getHashes();

                String savedMd5 = null;
                if (MapUtils.isNotEmpty(map)) {
                    Map<String, Boolean> hashAndResult = map.get(fileName);
                    if (MapUtils.isNotEmpty(hashAndResult)) {
                        savedMd5 = hashAndResult.keySet().iterator().next();
                    }
                }
                if (map == null) {
                    map = new HashMap<>();
                }

                if (map.isEmpty() || !md5.equals(savedMd5)) {
                //    log.debug(">>>>>>>>>>>>>>>>>>>>>> " + savedMd5);
                /*    Map<String, Boolean> innerMap = map.get(fileName);
                    map.put(fileName, md5);
                    try {
                        processAutoImportObject(importObject);
                        autoImportConfiguration.setHashes(map);
                    } catch (Exception e) {
                        log.warn("", e);
                    }*/
                }
            }
        } finally {
            authentication.end();
        }
    }

    protected String getHashByPath(String filePath) {
        InputStream stream = resources.getResourceAsStream(filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        byte[] fileBytes = new byte[0];
        if (stream != null) {
            try {
                fileBytes = IOUtils.toByteArray(stream);
            } catch (IOException e) {
                throw new AutoImportException("Unable to read" + fileName + "file", e);
            }
        } else {
            log.warn("File {} not found.", fileName);
        }
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(fileBytes);
    }

    protected void processAutoImportObject(AutoImportObject importObject) throws Exception {

        AutoImportProcessor autoImportProcessor;
        if (importObject.bean != null) {
            log.info("Importing file {} by bean {}", importObject.path, importObject.bean);
            autoImportProcessor = AppBeans.get(importObject.bean);
            autoImportProcessor.processFile(importObject.path);
            log.info("Successful importing file {} by bean {}", importObject.path, importObject.bean);
        } else if (importObject.importClass != null) {
            log.info("Importing file {} by class {}", importObject.path, importObject.importClass);
            autoImportProcessor =
                    (AutoImportProcessor) Class.forName(importObject.importClass).newInstance();
            autoImportProcessor.processFile(importObject.path);
            log.info("Successful importing file {} by class {}", importObject.path, importObject.importClass);
        }

    }

    @Override
    public void applicationStopped() {
    }
}
