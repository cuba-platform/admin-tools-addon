package com.haulmont.addon.admintools.processors;

import java.io.InputStream;

public interface AutoImportProcessor {

    void processFile(String filePath);
    void processFile(InputStream inputStream);
}
