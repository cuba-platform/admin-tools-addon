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
