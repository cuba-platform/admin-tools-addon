package com.haulmont.addon.admintools.core.auto_import;

public class AutoImportException extends RuntimeException {

    public AutoImportException() {
    }

    public AutoImportException(String message) {
        super(message);
    }

    public AutoImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
