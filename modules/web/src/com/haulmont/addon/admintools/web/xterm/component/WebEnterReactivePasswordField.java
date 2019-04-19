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
