package com.haulmont.addon.admintools.global.auto_import.type;

import com.google.gson.*;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportFileObjects;
import com.haulmont.cuba.core.config.type.TypeFactory;
import com.haulmont.cuba.core.config.type.TypeStringify;

public class AutoImportType {

    public static Gson generateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
    //    gsonBuilder.registerTypeAdapter()
        return gsonBuilder.create();
    }

    public static class Factory extends TypeFactory {
        @Override
        public Object build(String string) {
            return generateGson().fromJson(string, ImportFileObjects.class);
        }
    }

    public static class Stringify extends TypeStringify {
        @Override
        public String stringify(Object value) {
            return generateGson().toJson(value, ImportFileObjects.class);
        }
    }


}
