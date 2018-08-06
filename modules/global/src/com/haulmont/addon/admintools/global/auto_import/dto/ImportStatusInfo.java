package com.haulmont.addon.admintools.global.auto_import.dto;

import java.io.Serializable;

public class ImportStatusInfo implements Serializable {

    protected String hex;
    protected ImportStatus importStatus;

    @SuppressWarnings("unused")
    public ImportStatusInfo() {
    }

    public ImportStatusInfo(String hex, ImportStatus importStatus) {
        this.hex = hex;
        this.importStatus = importStatus;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }
}
