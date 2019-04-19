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

package com.haulmont.addon.admintools.gui.xterm;

import com.haulmont.addon.admintools.gui.xterm.components.XtermJs;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class XtermJsLoader extends AbstractComponentLoader<XtermJs> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(XtermJs.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        assignXmlDescriptor(resultComponent, element);

        loadVisible(resultComponent, element);
        loadEditable(resultComponent, element);
        loadEnable(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadHeight(resultComponent, element);
        loadWidth(resultComponent, element);
        loadAlign(resultComponent, element);
        loadResponsive(resultComponent, element);

        resultComponent.fit();
    }
}
