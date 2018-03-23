package com.haulmont.addon.admintools.web.screens.console

import com.haulmont.cuba.gui.components.SourceCodeEditor
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleFrame

import javax.inject.Inject

class ConsoleFrameExtended extends ConsoleFrame {
    @Inject
    SourceCodeEditor console

    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        console.setValue(params.getOrDefault('script', ''))
        this.setHeightFull()
        this.setWidthFull()
    }
}
