package com.haulmont.addon.admintools.web.screens.jpql

import com.haulmont.bali.util.ParamsMap
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
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

    @Override
    void init(Map<String, Object> params) {
        consoleFrame = openFrame(
                consoleFrame,
                'console-frame',
                ParamsMap.of('diagnoseType', DiagnoseType.JPQL)
        )
    }
}
