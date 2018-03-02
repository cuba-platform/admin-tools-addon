package com.haulmont.addon.admintools.console;

import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
public class ConsoleException extends RuntimeException {

    public ConsoleException() {
    }

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
