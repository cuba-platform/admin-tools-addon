package com.haulmont.addon.admintools.processors;

/**
 * Interface to be implemented by a custom processor. See full documentation in the README.md
 * of the Admin-tools component
 */
public interface AutoImportProcessor {

    /**
     * @param filePath is a classpath to a file
     */
    void processFile(String filePath) throws Exception;
}
