package com.haulmont.addon.admintools.web.gui.components;

import com.haulmont.addon.admintools.gui.components.XtermJs;
import com.haulmont.addon.admintools.web.toolkit.ui.xtermjs.XtermJsComponent;
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
    public void setRowsCountListener(RowsCountListener listener) {
        component.setRowsCountListener(listener);
    }

    @Override
    public RowsCountListener getRowsCountListener() {
        return component.getRowsCountListener();
    }
}