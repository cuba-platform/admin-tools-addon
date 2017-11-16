package com.haulmont.addon.admintools.config.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.cuba.core.config.type.TypeFactory;
import com.haulmont.cuba.core.config.type.TypeStringify;

import java.util.Map;

public class AutoImportType {

    public static Gson generateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.enableComplexMapKeySerialization().create();
    }

    public static class Factory extends TypeFactory {
        @Override
        public Object build(String string) {
            return generateGson().fromJson(string, Map.class);
        }
    }

    public static class Stringify extends TypeStringify {
        @Override
        public String stringify(Object value) {
            return generateGson().toJson(value, Map.class);
        }
    }
}
