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

package de.diedavids.cuba.runtimediagnose.web.screens.jpql

import com.haulmont.bali.util.ParamsMap
import com.haulmont.cuba.core.global.RemoteException
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.components.actions.BaseAction
import de.diedavids.cuba.runtimediagnose.db.DbDiagnoseService
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleWindow

import javax.inject.Inject

class JpqlConsole extends ConsoleWindow {

    @Inject
    DbDiagnoseService dbDiagnoseService

    @Inject
    UiComponents uiComponents

    @Override
    void init(Map<String, Object> params) {
        super.init(params)
        addSqlQueryButton()
    }

    @Override
    DiagnoseType getDiagnoseType() {
        return DiagnoseType.JPQL
    }

    void addSqlQueryButton() {
        Button showSqlBtn = uiComponents.create(Button)
        showSqlBtn.with {
            caption = getMessage('generateSql')
            setAction(new BaseAction('showSqlDialog').withHandler { e -> showSqlDialog() })
        }

        getComponent('consoleInstrumentalPanel').add(showSqlBtn)
    }

    void showSqlDialog() {
        SourceCodeEditor codeEditor = getComponent('console') as SourceCodeEditor

        if (codeEditorIsEmpty(codeEditor)) {
            showNotification(getMessage('noScriptDefined'), Frame.NotificationType.WARNING)
            return
        }

        try {
            String sqlQuery = dbDiagnoseService.getSqlQuery(codeEditor.value)

            if (sqlQuery) {
                String clearQuery = removeFirstAndLastSquareBrackets(sqlQuery)
                openWindow('sqlCopyDialog', WindowManager.OpenType.DIALOG, ParamsMap.of('sqlQuery', clearQuery))
            }
        }
        catch (RemoteException e) {
            def illegalArgumentException = e.causes.any {
                cause -> cause.className == 'java.lang.IllegalArgumentException'
            }
            if (illegalArgumentException) {
                showNotification(getMessage('wrongScript'), Frame.NotificationType.ERROR)
            }
        }
    }

    String removeFirstAndLastSquareBrackets(String query) {
        query.replaceAll('^\\[?', '')
                .replaceAll(']$', '')
    }

    boolean codeEditorIsEmpty(SourceCodeEditor codeEditor) {
        codeEditor && !codeEditor.value
    }
}
