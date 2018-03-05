package com.haulmont.addon.admintools.web.xterm.js;

import com.haulmont.addon.admintools.gui.xterm.components.XtermJs.RowsCountListener;
import com.haulmont.addon.admintools.gui.xterm.components.XtermJs.DataListener;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"xtermjs-connector.js", "dist/xterm.js", "dist/addons/fit/fit.js"})
@StyleSheet({"dist/xterm.css"})
public class XtermJsComponent extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = -3510903164972910396L;

    protected DataListener dataListener;
    protected RowsCountListener rowsCountListener;

    public XtermJsComponent() {
        addFunction("data", arguments -> {
            if (dataListener != null) {
                dataListener.data(arguments.getString(0));
            }
        });
        addFunction("rows", arguments -> {
            if (rowsCountListener != null) {
                rowsCountListener.changeRowsCount((int) arguments.getNumber(0));
            }
        });
    }

    public void write(String text) {
        callFunction("write", text);
    }

    public void writeln(String text) {
        callFunction("writeln", text);
    }

    public void fit() {
        callFunction("fit");
    }

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    public RowsCountListener getRowsCountListener() {
        return rowsCountListener;
    }

    public void setRowsCountListener(RowsCountListener listener) {
        this.rowsCountListener = listener;
    }

    @Override
    protected XtermJsState getState() {
        return (XtermJsState) super.getState();
    }


}