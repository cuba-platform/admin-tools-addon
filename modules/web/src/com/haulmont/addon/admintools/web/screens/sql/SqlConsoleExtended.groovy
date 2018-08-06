package com.haulmont.addon.admintools.web.screens.sql

import com.haulmont.bali.util.ParamsMap
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.SourceCodeEditor
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.sql.SqlConsole

import javax.inject.Inject

class SqlConsoleExtended extends SqlConsole{

    @Inject
    protected Frame consoleFrame
    @Inject
    protected SourceCodeEditor console

    @Override
    void init(Map<String, Object> params) {
        params.put('diagnoseType', DiagnoseType.SQL)
        openFrame(consoleFrame, 'console-frame', params)
    }
}
