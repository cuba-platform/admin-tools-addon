/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.admintools.global.auto_import.type;

import com.google.gson.*;
import com.haulmont.addon.admintools.global.auto_import.dto.ImportedFilesInfo;
import com.haulmont.cuba.core.config.type.TypeFactory;
import com.haulmont.cuba.core.config.type.TypeStringify;

public class AutoImportType {

    public static Gson generateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    public static class Factory extends TypeFactory {
        @Override
        public Object build(String string) {
            return generateGson().fromJson(string, ImportedFilesInfo.class);
        }
    }

    public static class Stringify extends TypeStringify {
        @Override
        public String stringify(Object value) {
            return generateGson().toJson(value, ImportedFilesInfo.class);
        }
    }


}
