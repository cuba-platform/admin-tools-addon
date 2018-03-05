package com.haulmont.addon.admintools.core.auto_import.listeners;

@SuppressWarnings("unused")
public interface AutoImportListenerDelegate {

    String NAME = "autoimport_AutoImportListenerDelegate";

    void applicationStarted();

    void applicationStopped();
}
