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