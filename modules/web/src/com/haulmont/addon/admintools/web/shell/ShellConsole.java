package com.haulmont.addon.admintools.web.shell;

import com.haulmont.addon.admintools.console.ConsoleService;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.ProgressBar;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.actions.ExcelAction;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.BackgroundTaskHandler;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.IllegalConcurrentAccessException;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import de.diedavids.cuba.runtimediagnose.SqlConsoleSecurityException;
import de.diedavids.cuba.runtimediagnose.db.DbQueryResult;
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionFactory;
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType;
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleWindow;
import de.diedavids.cuba.runtimediagnose.web.screens.diagnose.DiagnoseFileDownloader;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ShellConsole extends ConsoleWindow {

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected ConsoleService consoleService;

    @Inject
    protected BackgroundWorker backgroundWorker;

    @Inject
    protected TextField args;

    @Inject
    protected Action runSqlConsoleAction;

    @Inject
    protected Action cancelSqlConsoleAction;

    @Inject
    protected SourceCodeEditor console;

    @Inject
    protected SourceCodeEditor consoleResult;

    @Inject
    protected ProgressBar runningBar;

    protected BackgroundTaskHandler handler;

    // For this it is not needed
    @Override
    public DiagnoseType getDiagnoseType() {
        return null;
    }

    @Override
    public void doRunConsole() {
        BackgroundTask<Integer, String> shellTask = new BackgroundTask<Integer, String>(600, this) {

            @Override
            public String run(TaskLifeCycle<Integer> taskLifeCycle) throws IOException {
                return consoleService.executeShell("", console.getValue(), args.getValue());
            }

            @Override
            public void progress(List<Integer> changes) {
                taskIsRunning();
            }

            @Override
            public void done(String result) {
                if (result != null) {
                    consoleResult.setValue(result);
                }

                taskIsStopped();
            }
        };

        try {
            handler = backgroundWorker.handle(shellTask);
            handler.execute();

            taskIsRunning();
        } catch (IllegalConcurrentAccessException e) {
            taskIsStopped();
            throw e;
        }
    }

    @Override
    public void clearConsoleResult() {
        // todo: implement
    }

    protected void taskIsRunning() {
        runningBar.setIndeterminate(true);
    }

    protected void taskIsStopped() {
        runningBar.setIndeterminate(false);
    }


    @Override
    public void downloadConsoleResult() {
    }

    @Override
    public void downloadDiagnoseRequestFile() {
    }

    public void cancelConsole() {
    }
}