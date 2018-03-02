package com.haulmont.addon.admintools.console;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component("admintools_ConsoleExceptionHandler")
public class ConsoleExceptionHandler extends AbstractGenericExceptionHandler {

    public ConsoleExceptionHandler() {
        super(ConsoleException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message, Frame.NotificationType.ERROR);
    }
}
