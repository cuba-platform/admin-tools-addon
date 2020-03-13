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

        console.setValue(params.getOrDefault('script', ''))
    }
}
