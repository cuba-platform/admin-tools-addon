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

package de.diedavids.cuba.runtimediagnose.wizard;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DiagnoseWizardResultType implements EnumClass<String> {

    ERROR("ERROR", "font-icon:EXCLAMATION"),
    WARNING("WARNING", "font-icon:INFO"),
    SUCCESS("SUCCESS", "font-icon:CHECK");

    private String id;

    private String icon;

    DiagnoseWizardResultType(String value, String icon) {
        this.id = value;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    @Nullable
    public static DiagnoseWizardResultType fromId(String id) {
        for (DiagnoseWizardResultType at : DiagnoseWizardResultType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}