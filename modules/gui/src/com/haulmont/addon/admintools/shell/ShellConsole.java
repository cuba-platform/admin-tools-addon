package com.haulmont.addon.admintools.shell;

import com.haulmont.addon.admintools.console.ConsoleService;
import com.haulmont.cuba.core.global.RemoteException;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.BackgroundTaskHandler;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

public class ShellConsole extends AbstractWindow {

    @Named("shellText")
    protected SourceCodeEditor shellText;

    @Named("args")
    protected TextArea args;

    @Inject
    protected ConsoleService consoleService;

    @Inject
    protected BackgroundWorker backgroundWorker;


    public void shellRun() {
        BackgroundTask shellTask = new BackgroundTask<Integer, String>(600, this) {
            @Override
            public String run(TaskLifeCycle<Integer> taskLifeCycle) throws IOException {
                return consoleService.executeShell("", shellText.getValue(), args.getValue());
            }

            @Override
            public void done(String result) {
                showNotification("Done!\nConsole output:\n" + (result == null ? "" : result));
            }
        };

        BackgroundTaskHandler handler = backgroundWorker.handle(shellTask);
        handler.execute();
    }
}