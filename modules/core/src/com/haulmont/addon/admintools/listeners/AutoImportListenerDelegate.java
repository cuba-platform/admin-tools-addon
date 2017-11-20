package com.haulmont.addon.admintools.listeners;

@SuppressWarnings("unused")
public interface AutoImportListenerDelegate {

    String NAME = "autoimport_AutoImportListenerDelegate";

    void applicationStarted();

    void applicationStopped();
}
