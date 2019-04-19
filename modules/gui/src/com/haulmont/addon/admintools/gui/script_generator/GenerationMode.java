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

package com.haulmont.addon.admintools.gui.script_generator;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum GenerationMode implements EnumClass<Integer> {

    SELECTED_ENTITIES(10),
    CUSTOM_QUERY(20);

    protected Integer id;

    GenerationMode(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static GenerationMode fromId(Integer id) {
        for (GenerationMode at : GenerationMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}