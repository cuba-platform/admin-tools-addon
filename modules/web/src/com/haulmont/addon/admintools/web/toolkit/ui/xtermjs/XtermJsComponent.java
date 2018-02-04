package com.haulmont.addon.admintools.web.toolkit.ui.xtermjs;

import com.haulmont.addon.admintools.gui.components.XtermJs;
import com.haulmont.addon.admintools.gui.components.XtermJs.DataListener;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.annotations.JavaScript;

@JavaScript({"xtermjs-connector.js", "dist/xterm.js", "dist/addons/fit/fit.js"})
@StyleSheet({"dist/xterm.css"})
public class XtermJsComponent extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = -3510903164972910396L;

    protected DataListener listener;

    public XtermJsComponent() {
        addFunction("data", arguments -> {
            if (listener != null) {
                listener.data(arguments.getString(0));
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
        return listener;
    }

    public void setDataListener(DataListener listener) {
        this.listener = listener;
    }

    @Override
    protected XtermJsState getState() {
        return (XtermJsState) super.getState();
    }


}