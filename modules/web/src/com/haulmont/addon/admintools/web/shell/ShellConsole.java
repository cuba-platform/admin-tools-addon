package com.haulmont.addon.admintools.web.shell;

import com.haulmont.addon.admintools.console.ConsoleBean;
import com.haulmont.addon.admintools.console.ConsolePrecondition;
import com.haulmont.addon.admintools.console.ConsoleTool;
import com.haulmont.addon.admintools.web.utils.NonBlockingIOUtils;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType;
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleWindow;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ShellConsole extends ConsoleWindow {

    @Inject
    protected ConsoleBean consoleBean;
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
    @Inject
    protected Timer timer;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected ConsoleTool consoleTool;
    @Inject
    protected ConsolePrecondition precondition;

    protected Process shellProcess;
    protected StringBuilder resultOutBuilder = new StringBuilder();
    protected NonBlockingIOUtils ioUtils = new NonBlockingIOUtils();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (!precondition.isOsUnix()) {
            showNotification(getMessage("shellNotSupported"), ERROR);
        }

        timer.addStopListener(t -> {
            runningBar.setIndeterminate(false);
            resultOutBuilder.setLength(0);

            if (isShellProcessAlive()) {
                shellProcess.destroy();
            }
        });
    }

    // For this it is not needed
    @Override
    public DiagnoseType getDiagnoseType() {
        return null;
    }

    @Override
    public void doRunConsole() {

        if (isShellProcessAlive()) {
            showOptionDialog(
                    getMessage("consoleDialogTitle"),
                    getMessage("restartDialogMessage"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK).withHandler(e -> executeScript()),
                            new DialogAction(DialogAction.Type.CANCEL)
                    }
            );
        } else {
            executeScript();
        }
    }

    @Override
    public void clearConsoleResult() {
        consoleResult.setValue("");
    }

    @Override
    public void downloadDiagnoseRequestFile() {
        exportToFile("console_request.sh", console.getValue());
    }

    @Override
    public void downloadConsoleResult() {
        exportToFile("console_result.txt", consoleResult.getValue());
    }

    @Override
    protected boolean preClose(String actionId) {
        if (isShellProcessAlive()) {
            shellProcess.destroy();
        }
        return super.preClose(actionId);
    }

    public void cancelConsole() {
        if (isShellProcessAlive()) {
            showOptionDialog(
                    getMessage("consoleDialogTitle"),
                    getMessage("cancelDialogMessage"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK).withHandler(e -> timer.stop()),
                            new DialogAction(DialogAction.Type.CANCEL)
                    }
            );
        }
    }

    public void printResult(Timer source) {
        if (shellProcess == null) {
            return;
        }

        try {
            String text = ioUtils.toStringWithBarrier(shellProcess.getInputStream(), UTF_8, 100);
            resultOutBuilder.append(text);
            consoleResult.setValue(resultOutBuilder.toString());
        } catch (IOException e) {
            showNotification(e.getLocalizedMessage(), ERROR);
        } finally {
            timer.stop();
        }
    }

    protected void executeScript() {
        if (!precondition.isOsUnix()) {
            showNotification(getMessage("shellNotSupported"), ERROR);
            return;
        }

        timer.stop();

        String script = isNotBlank(console.getValue()) ? console.getValue() : "";
        String arguments = isNotBlank(args.getValue()) ? args.getValue() : "";
        List<String> parsedArgs = consoleTool.parseArgs(arguments);

        try {
            shellProcess = consoleBean.execute(script, parsedArgs);
            runningBar.setIndeterminate(true);
            timer.start();
        } catch (IOException e) {
            showNotification(e.getLocalizedMessage(), ERROR);
            timer.stop();
        }
    }

    protected void exportToFile(String filename, String content) {
        if (isNotBlank(content)) {
            byte[] bytes = content.getBytes(UTF_8);
            exportDisplay.show(new ByteArrayDataProvider(bytes), filename);
        }
    }

    protected boolean isShellProcessAlive() {
        return shellProcess != null && shellProcess.isAlive();
    }

}