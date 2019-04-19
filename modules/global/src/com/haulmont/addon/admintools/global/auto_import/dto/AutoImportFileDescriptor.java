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
