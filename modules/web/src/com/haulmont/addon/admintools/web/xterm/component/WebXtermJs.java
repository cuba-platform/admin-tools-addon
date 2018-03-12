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