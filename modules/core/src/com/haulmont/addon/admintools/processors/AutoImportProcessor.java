package com.haulmont.addon.admintools.processors;

public interface AutoImportProcessor {

    void processFile(String filePath) throws Exception;
}
