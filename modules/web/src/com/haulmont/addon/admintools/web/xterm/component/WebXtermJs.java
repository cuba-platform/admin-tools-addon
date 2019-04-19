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

package com.haulmont.addon.admintools.web.xterm.component;

import com.haulmont.addon.admintools.gui.xterm.components.XtermJs;
import com.haulmont.addon.admintools.web.xterm.js.XtermJsComponent;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public class WebXtermJs extends WebAbstractComponent<XtermJsComponent> implements XtermJs {

    public WebXtermJs() {
        this.component = new XtermJsComponent();
    }

    @Override
    public void write(String text) {
        component.write(text);
    }

    @Override
    public void writeln(String text) {
        component.writeln(text);
    }

    @Override
    public void fit() {
        component.fit();
    }

    @Override
    public DataListener getDataListener() {
        return component.getDataListener();
    }

    @Override
    public void setDataListener(DataListener listener) {
        component.setDataListener(listener);
    }

    @Override
    public void setSizeListener(TerminalSizeListener listener) {
        component.setTerminalSizeListener(listener);
    }

    @Override
    public TerminalSizeListener getSizeListener() {
        return component.getTerminalSizeListener();
    }
}