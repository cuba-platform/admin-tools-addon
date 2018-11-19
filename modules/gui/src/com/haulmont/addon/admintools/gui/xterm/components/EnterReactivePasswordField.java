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
