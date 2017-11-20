package com.haulmont.addon.admintools.config.type;

import com.google.gson.*;
import com.haulmont.addon.admintools.dto.ImportDataObject;
import com.haulmont.addon.admintools.dto.ImportFileObjects;
import com.haulmont.cuba.core.config.type.TypeFactory;
import com.haulmont.cuba.core.config.type.TypeStringify;

import java.lang.reflect.Type;
import java.util.Map;

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
