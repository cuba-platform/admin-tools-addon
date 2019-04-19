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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.StandardCloseAction;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class ScriptGenerator extends AbstractWindow {

    protected static final String PARAMETER_SELECTED_ENTITIES = "selectedEntities";
    protected static final String GENERATION_MODE = "generationMode";

    @WindowParam(name = PARAMETER_SELECTED_ENTITIES)
    protected Collection selectedEntities;

    @Inject
    protected OptionsGroup generationMode;

    @Inject
    protected Screens screens;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        generationMode.setOptionsEnum(GenerationMode.class);
        generationMode.setValue(GenerationMode.SELECTED_ENTITIES);
    }

    public void windowCommit(){
        screens.create("admintools$generateScriptsResult", OpenMode.DIALOG,new MapScreenOptions(ParamsMap.of(
                PARAMETER_SELECTED_ENTITIES, selectedEntities,
                GENERATION_MODE, generationMode.getValue()
        ))).show();
        windowClose();
    }

    public void windowClose(){
        close(new StandardCloseAction(CLOSE_ACTION_ID));
    }
}