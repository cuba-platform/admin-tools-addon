package com.haulmont.addon.admintools.web.xterm.js;

import com.haulmont.addon.admintools.gui.xterm.components.XtermJs.TerminalSizeListener;
import com.haulmont.addon.admintools.gui.xterm.components.XtermJs.DataListener;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"xtermjs-connector.js", "dist/xterm.js", "dist/addons/fit/fit.js"})
@StyleSheet({"dist/xterm.css"})
public class XtermJsComponent extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = -3510903164972910396L;

    protected DataListener dataListener;
    protected TerminalSizeListener terminalSizeListener;

    public XtermJsComponent() {
        addFunction("data", arguments -> {
            if (dataListener != null) {
                dataListener.data(arguments.getString(0));
            }
        });
        addFunction("size", arguments -> {
            if (terminalSizeListener != null) {
                terminalSizeListener.changeSize((int) arguments.getNumber(0), (int) arguments.getNumber(1));
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

    public TerminalSizeListener getTerminalSizeListener() {
        return terminalSizeListener;
    }

    public void setTerminalSizeListener(TerminalSizeListener listener) {
        this.terminalSizeListener = listener;
    }

    @Override
    protected XtermJsState getState() {
        return (XtermJsState) super.getState();
    }


}