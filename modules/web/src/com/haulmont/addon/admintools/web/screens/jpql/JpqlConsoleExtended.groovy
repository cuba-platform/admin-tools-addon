package com.haulmont.addon.admintools.web.screens.jpql

import com.haulmont.cuba.web.gui.components.WebSourceCodeEditor
import de.diedavids.cuba.runtimediagnose.db.DbDiagnoseService
import de.diedavids.cuba.runtimediagnose.web.screens.jpql.JpqlConsole

import javax.inject.Inject

class JpqlConsoleExtended extends JpqlConsole {

    @Inject
    DbDiagnoseService dbDiagnoseService

    @Inject
    JpqlSuggesterHelper suggesterHelper

    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        WebSourceCodeEditor console = getComponent("console") as WebSourceCodeEditor
        console.setSuggester({ source, text, cursorPosition ->
            suggesterHelper.getHint(console, text, cursorPosition)
        })
    }
}
