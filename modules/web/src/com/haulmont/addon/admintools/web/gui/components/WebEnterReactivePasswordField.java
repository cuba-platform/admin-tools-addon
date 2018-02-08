package com.haulmont.addon.admintools.web.gui.components;

import com.haulmont.addon.admintools.gui.components.EnterReactivePasswordField;
import com.haulmont.cuba.web.gui.components.WebAbstractTextField;
import com.haulmont.cuba.web.toolkit.ui.CubaPasswordField;
import com.vaadin.event.ShortcutListener;

public class WebEnterReactivePasswordField extends WebAbstractTextField<CubaPasswordField> implements EnterReactivePasswordField {

    protected ShortcutListener enterShortcutListener;

    @Override
    protected CubaPasswordField createTextFieldImpl() {
        return new CubaPasswordField();
    }

    @Override
    public int getMaxLength() {
        return component.getMaxLength();
    }

    @Override
    public void setMaxLength(int value) {
        component.setMaxLength(value);
    }

    @Override
    public boolean isAutocomplete() {
        return component.isAutocomplete();
    }

    @Override
    public void setAutocomplete(Boolean value) {
        component.setAutocomplete(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public void addEnterPressListener(EnterPressListener listener) {
        getEventRouter().addListener(EnterPressListener.class, listener);

        if (enterShortcutListener == null) {
            enterShortcutListener = new ShortcutListener("", com.vaadin.event.ShortcutAction.KeyCode.ENTER, null) {
                @Override
                public void handleAction(Object sender, Object target) {
                    EnterPressEvent event = new EnterPressEvent(WebEnterReactivePasswordField.this);
                    getEventRouter().fireEvent(EnterPressListener.class, EnterPressListener::enterPressed, event);
                }
            };
            component.addShortcutListener(enterShortcutListener);
        }
    }

    @Override
    public void removeEnterPressListener(EnterPressListener listener) {
        getEventRouter().removeListener(EnterPressListener.class, listener);

        if (enterShortcutListener != null && !getEventRouter().hasListeners(EnterPressListener.class)) {
            component.removeShortcutListener(enterShortcutListener);
        }
    }
}
