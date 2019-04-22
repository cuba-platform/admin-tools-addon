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

package de.diedavids.cuba.runtimediagnose.groovy

import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.global.*
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecution
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionLogService
import de.diedavids.cuba.runtimediagnose.groovy.binding.GroovyScriptBindingSupplier
import org.springframework.stereotype.Service

import javax.inject.Inject

@SuppressWarnings('DuplicateStringLiteral')
@Service(GroovyDiagnoseService.NAME)
class GroovyDiagnoseServiceBean implements GroovyDiagnoseService {

    @Inject
    Scripting scripting

    @Inject
    DataManager dataManager

    @Inject
    Metadata metadata

    @Inject
    DatatypeFormatter datatypeFormatter

    @Inject
    TimeSource timeSource

    @Inject
    Persistence persistence

    @Inject
    UserSessionSource userSessionSource

    @Inject
    DiagnoseExecutionLogService diagnoseExecutionLogService

    @Inject
    List<GroovyScriptBindingSupplier> groovyScriptBindingSuppliers



    DiagnoseExecution runGroovyDiagnose(DiagnoseExecution diagnoseExecution) {
        if (diagnoseExecution) {
            def log = new GroovyConsoleLogger(timeSource: timeSource, datatypeFormatter: datatypeFormatter)
            Binding binding = createBinding(log)
            diagnoseExecution.executionTimestamp = timeSource.currentTimestamp()
            diagnoseExecution.executionUser = userSessionSource.userSession.currentOrSubstitutedUser.login

            try {
                def result = scripting.evaluateGroovy(diagnoseExecution.diagnoseScript, binding)
                diagnoseExecution.handleSuccessfulExecution(result.toString())
            }

            catch (Exception e) {
                diagnoseExecution.handleErrorExecution(e)
            }
            diagnoseExecution.addResult('log', log.toString())

            diagnoseExecutionLogService.logDiagnoseExecution(diagnoseExecution)

            diagnoseExecution
        }
    }

    protected Binding createBinding(GroovyConsoleLogger log) {
        def binding = scriptBinding

        binding.setVariable('log', log)

        /**
         * for backwards comparible reasons.
         * TODO: remove in 1.2.0
         */
        additionalBindingVariableMap.each { k, v ->
            binding.setVariable(k, v)
        }
        binding
    }


    protected Binding getScriptBinding() {

        Map<String, Object> bindingValues = [:]
        groovyScriptBindingSuppliers.each {
            bindingValues += it.binding
        }

        new Binding(bindingValues)
    }


    /**
     * @deprecated use GroovyScriptBindingSupplier instead
     */
    @Deprecated
    protected Map<String, Object> getAdditionalBindingVariableMap() {
        [:]
    }
}