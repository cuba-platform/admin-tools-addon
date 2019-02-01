package com.haulmont.addon.admintools.gui.console;

import com.haulmont.addon.admintools.global.console.ConsoleException;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.exception.AbstractUiExceptionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component("admintools_ConsoleExceptionHandler")
public class ConsoleExceptionHandler extends AbstractUiExceptionHandler {

    public ConsoleExceptionHandler() {
        super(ConsoleException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, UiContext context) {
        context.getNotifications().create(Notifications.NotificationType.ERROR)
                .withCaption(message)
                .show();
    }
}
