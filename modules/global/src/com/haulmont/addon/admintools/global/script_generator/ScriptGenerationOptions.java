package com.haulmont.addon.admintools.global.script_generator;

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import javax.annotation.Nullable;

public enum ScriptGenerationOptions implements EnumClass<Integer> {

    INSERT(10),
    UPDATE(20),
    INSERT_UPDATE(30);

    protected Integer id;

    ScriptGenerationOptions(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ScriptGenerationOptions fromId(Integer id) {
        for (ScriptGenerationOptions at : ScriptGenerationOptions.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}