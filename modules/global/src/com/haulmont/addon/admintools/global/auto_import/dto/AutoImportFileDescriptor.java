package com.haulmont.addon.admintools.global.auto_import.dto;

import javax.annotation.Nullable;

public class AutoImportFileDescriptor {
    protected final String path;
    protected final String bean;
    protected final String importClass;

    public AutoImportFileDescriptor(String path, @Nullable String bean, @Nullable String importClass) {
        this.path = path;
        this.bean = bean;
        this.importClass = importClass;
    }

    public String getPath() {
        return path;
    }

    @Nullable
    public String getBean() {
        return bean;
    }

    @Nullable
    public String getImportClass() {
        return importClass;
    }

    @Override
    public String toString() {
        return "AutoImportFileDescriptor{" +
                "path='" + path + '\'' +
                ", bean='" + bean + '\'' +
                ", importClass='" + importClass + '\'' +
                '}';
    }
}
