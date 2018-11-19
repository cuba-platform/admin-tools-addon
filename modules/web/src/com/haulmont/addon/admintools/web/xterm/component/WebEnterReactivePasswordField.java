package com.haulmont.addon.admintools.web.xterm.component;

import com.haulmont.addon.admintools.gui.xterm.components.EnterReactivePasswordField;
import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.web.gui.components.WebPasswordField;
import com.haulmont.cuba.web.gui.components.util.ShortcutListenerDelegate;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;

import java.util.function.Consumer;

public class WebEnterReactivePasswordField extends WebPasswordField implements EnterReactivePasswordField {

    protected ShortcutListener enterShortcutListener;

    @SuppressWarnings("unchecked")
    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public Subscription addEnterPressListener(Consumer<EnterPressEvent> listener) {
        if (enterShortcutListener == null) {
            enterShortcutListener = new ShortcutListenerDelegate("", ShortcutAction.KeyCode.ENTER, null)
                    .withHandler((sender, target) -> {
                        EnterPressEvent event = new EnterPressEvent(WebEnterReactivePasswordField.this);
                        publish(EnterPressEvent.class, event);
                    });
            component.addShortcutListener(enterShortcutListener);
        }

        getEventHub().subscribe(EnterPressEvent.class, listener);

        return () -> removeEnterPressListener(listener);
    }

    @Override
    public void removeEnterPressListener(Consumer<EnterPressEvent> listener) {
        unsubscribe(EnterPressEvent.class, listener);

        if (enterShortcutListener != null
                && !hasSubscriptions(EnterPressEvent.class)) {
            component.removeShortcutListener(enterShortcutListener);
            enterShortcutListener = null;
        }
    }
}
