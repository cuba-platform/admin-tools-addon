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

package com.haulmont.addon.admintools.gui.xterm.components;

import com.haulmont.cuba.gui.components.Component;

/**
 * Interface to provide an adapter for the Xterm JS terminal
 */
public interface XtermJs extends Component {
    String NAME = "xterm-js";

    /**
     * Print the {@code text} on the terminal
     */
    void write(String text);

    /**
     * Print the {@code text} on the terminal and then terminate the line.
     */
    void writeln(String text);

    /**
     * fit the terminal to the preferred size
     */
    void fit();

    void setDataListener(DataListener listener);

    DataListener getDataListener();

    void setSizeListener(TerminalSizeListener listener);

    TerminalSizeListener getSizeListener();

    /**
     * Interface which listen manual commands {@code data} for the terminal
     */
    interface DataListener {
        void data(String data);
    }

    /**
     * Interface which listen columns and rows count changes {@code data} for the terminal
     */
    interface TerminalSizeListener {
        void changeSize(int cols, int rows);
    }
}