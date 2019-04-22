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

import com.haulmont.cuba.gui.components.AbstractWindow
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.TextArea
import com.vaadin.ui.JavaScript

import javax.inject.Named

class SqlCopyDialog extends AbstractWindow {

    @Named('sqlTextArea')
    TextArea textArea

    @Named('copyBtn')
    Button copyBtn

    String copyButtonClass = 'copy-sql-button'
    String sqlTextContentClass = "sql-text-content-${UUID.randomUUID()}"

    @Override
    void init(Map<String, Object> params) {
        super.init(params)
        copyBtn.styleName = copyButtonClass
        textArea.styleName = sqlTextContentClass
        textArea.value = params.get('sqlQuery')

        initJavascriptButtonListener()
    }

    void initJavascriptButtonListener() {
        JavaScript.current.execute(addCopyButtonListener())
    }

    String addCopyButtonListener() {
        """
            try {
                var copyButton = document.querySelector('.${copyButtonClass}')
                copyButton.addEventListener('click', function(){
                    var textarea = document.querySelector('.${sqlTextContentClass}').querySelector('textarea');
                    textarea.select();
                    document.execCommand('copy');
                });
            }
            catch(err) {
                concole.log(err);
            }
        """
    }
}
