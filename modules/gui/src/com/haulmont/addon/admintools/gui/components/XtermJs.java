package com.haulmont.addon.admintools.gui.components;

import com.haulmont.cuba.gui.components.Component;

public interface XtermJs extends Component {
    String NAME = "xterm-js";

    void write(String text);

    void writeln(String text);

    void fit();

    void setDataListener(DataListener listener);

    DataListener getDataListener();

    interface DataListener {
        void data(String data);
    }
}