package com.haulmont.addon.admintools.web.screens.jpql

import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.haulmont.cuba.web.gui.components.WebSourceCodeEditor
import de.diedavids.cuba.runtimediagnose.db.DbDiagnoseService
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleFrame
import de.diedavids.cuba.runtimediagnose.web.screens.jpql.JpqlConsole

import javax.inject.Inject

class JpqlConsoleExtended extends JpqlConsole {

    @Inject
    DbDiagnoseService dbDiagnoseService
    @Inject
    ConsoleFrame consoleFrame
    @Inject
    ComponentsFactory componentsFactory
    @Inject
    JpqlSuggesterHelper suggesterHelper

    @Override
    void init(Map<String, Object> params) {
        params.put('diagnoseType', DiagnoseType.JPQL)
        def frame = openFrame(consoleFrame, 'console-frame', params)

        WebSourceCodeEditor console = frame.getComponent("console") as WebSourceCodeEditor
        console.setSuggester({ source, text, cursorPosition ->
            suggesterHelper.getHint(console, text, cursorPosition)
        })
    }
}
