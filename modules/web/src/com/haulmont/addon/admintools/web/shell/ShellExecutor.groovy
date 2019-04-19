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

package com.haulmont.addon.admintools.web.shell

import com.haulmont.addon.admintools.global.console.ConsoleBean
import com.haulmont.addon.admintools.global.console.ConsoleTools
import com.haulmont.addon.admintools.web.utils.NonBlockingIOUtils
import com.haulmont.cuba.gui.Dialogs
import com.haulmont.cuba.gui.Notifications
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.export.ByteArrayDataProvider
import com.haulmont.cuba.gui.export.ExportDisplay
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleWindow

import javax.inject.Inject

import static java.nio.charset.StandardCharsets.UTF_8
import static org.apache.commons.lang3.StringUtils.isNotBlank

class ShellExecutor extends ConsoleWindow {

    @Inject
    protected ConsoleBean consoleBean
    @Inject
    protected TextField<String> args
    @Inject
    protected Action runSqlConsoleAction
    @Inject
    protected Action cancelSqlConsoleAction
    @Inject
    protected SourceCodeEditor console
    @Inject
    protected SourceCodeEditor consoleResult
    @Inject
    protected ProgressBar runningBar
    @Inject
    protected Timer timer
    @Inject
    protected ExportDisplay exportDisplay
    @Inject
    protected ConsoleTools consoleTools
    @Inject
    protected Notifications notifications
    @Inject
    protected Dialogs dialogs

    protected Process shellProcess
    protected StringBuilder resultOutBuilder = new StringBuilder()
    protected NonBlockingIOUtils ioUtils = new NonBlockingIOUtils()

    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        if (!consoleTools.isOsUnix()) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(getMessage("shellNotSupported"))
                    .show()
        }

        timer.addTimerStopListener { t ->
            runningBar.setIndeterminate(false)
            resultOutBuilder.setLength(0)

            if (isShellProcessAlive()) {
                shellProcess.destroy()
            }
        }
    }

    // For this it is not needed
    @Override
    DiagnoseType getDiagnoseType() {
        return null
    }

    @Override
    void doRunConsole() {

        if (isShellProcessAlive()) {
            dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                    .withCaption(getMessage("consoleDialogTitle"))
                    .withMessage(getMessage("restartDialogMessage"))
                    .withActions(
                        new DialogAction(DialogAction.Type.OK).withHandler { e -> executeScript() },
                        new DialogAction(DialogAction.Type.CANCEL))
                    .show()
        } else {
            executeScript()
        }
    }

    @Override
    void clearConsoleResult() {
        consoleResult.setValue("")
    }

    @Override
    void downloadDiagnoseRequestFile() {
        exportToFile("console_request.sh", console.getValue())
    }

    @Override
    void downloadConsoleResult() {
        exportToFile("console_result.txt", consoleResult.getValue())
    }

    @Override
    protected boolean preClose(String actionId) {
        if (isShellProcessAlive()) {
            shellProcess.destroy()
        }
        return super.preClose(actionId)
    }

    void cancelConsole() {
        if (isShellProcessAlive()) {
            dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                    .withCaption(getMessage("consoleDialogTitle"))
                    .withMessage(getMessage("cancelDialogMessage"))
                    .withActions(
                    new DialogAction(DialogAction.Type.OK).withHandler { e -> timer.stop() },
                    new DialogAction(DialogAction.Type.CANCEL))
                    .show()
        }
    }

    void printResult(Timer source) {
        if (shellProcess == null) {
            return
        }

        try {
            String text = ioUtils.toStringWithBarrier(shellProcess.getInputStream(), UTF_8, 100)
            resultOutBuilder.append(text)
            consoleResult.setValue(resultOutBuilder.toString())
        } catch (IOException e) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(e.getLocalizedMessage())
                    .show()
        } finally {
            timer.stop()
        }
    }

    protected void executeScript() {
        if (!consoleTools.isOsUnix()) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(getMessage("shellNotSupported"))
                    .show()
            return
        }

        timer.stop()

        String script = isNotBlank(console.getValue()) ? console.getValue() : ""
        String arguments = isNotBlank(args.getValue()) ? args.getValue() : ""
        List<String> parsedArgs = consoleTools.parseArgs(arguments)

        try {
            shellProcess = consoleBean.execute(script, parsedArgs)
            runningBar.setIndeterminate(true)
            timer.start()
        } catch (IOException e) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(e.getLocalizedMessage())
                    .show()
            timer.stop()
        }
    }

    protected void exportToFile(String filename, String content) {
        if (isNotBlank(content)) {
            byte[] bytes = content.getBytes(UTF_8)
            exportDisplay.show(new ByteArrayDataProvider(bytes), filename)
        }
    }

    protected boolean isShellProcessAlive() {
        return shellProcess != null && shellProcess.isAlive()
    }

}