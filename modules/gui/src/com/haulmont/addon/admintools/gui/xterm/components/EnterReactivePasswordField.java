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

import com.haulmont.cuba.gui.components.PasswordField;
import com.haulmont.cuba.gui.components.TextInputField;

/**
 * The differences from PasswordField is possibility to add EnterPressListener.
 */
public interface EnterReactivePasswordField extends PasswordField, TextInputField.EnterPressNotifier, TextInputField.MaxLengthLimited  {

    String NAME = "enterReactivePasswordField";

    /**
     * Return autocomplete attribute value to specify saving it in browser.
     */
    boolean isAutocomplete();

    /**
     * Set autocomplete attribute value to specify saving it in browser.
     * False value disables saving passwords in browser.
     */
    void setAutocomplete(Boolean autocomplete);

    @SuppressWarnings("unchecked")
    @Override
    String getValue();
}
