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

package de.diedavids.cuba.runtimediagnose.diagnose

import groovy.transform.CompileStatic

@CompileStatic
class DiagnoseExecution implements Serializable{


    private static final long serialVersionUID = -8288852447591914153L

    static final String RESULT_STACKTRACE_NAME = 'stacktrace'
    static final String RESULT_LOG_NAME = 'log'
    static final String RESULT_NAME = 'result'

    Boolean executionSuccessful

    DiagnoseExecutionType executionType

    Date executionTimestamp

    String executionUser

    String diagnoseScript

    Map<String, String> diagnoseResults = [:]

    DiagnoseManifest manifest

    boolean isGroovy() {
        manifest.diagnoseType == DiagnoseType.GROOVY
    }

    boolean isSQL() {
        manifest.diagnoseType == DiagnoseType.SQL
    }

    boolean isJPQL() {
        manifest.diagnoseType == DiagnoseType.JPQL
    }

    String getExecutedScriptFileExtension() {
        String extension = ''
        if (manifest) {
            extension = manifest.diagnoseType.name().toLowerCase()
        }

        extension
    }

    Map<String, String> getExecutionResultFileMap() {

        def executionResultFileMap = [:]

        addResultFileIfPossible(executionResultFileMap as Map<String, String>, "diagnose.${executedScriptFileExtension}", diagnoseScript)
        addResultFileIfPossible(executionResultFileMap as Map<String, String>, 'result.log', getResult(RESULT_NAME))
        addResultFileIfPossible(executionResultFileMap as Map<String, String>, 'log.log', getResult(RESULT_LOG_NAME))
        addResultFileIfPossible(executionResultFileMap as Map<String, String>, 'stacktrace.log', getResult(RESULT_STACKTRACE_NAME))

        executionResultFileMap as Map<String, String>
    }

    private void addResultFileIfPossible(Map<String, String> executionResultFileMap, String filename, String fileContent) {
        if (fileContent) {
            executionResultFileMap[filename] = fileContent
        }
    }

    void addResult(String fileName, Object fileContent) {
        diagnoseResults[fileName] = fileContent.toString()
    }

    String getResult(String fileName) {
        diagnoseResults[fileName]
    }

    boolean isExecuted() {
        executionTimestamp != null
    }
    boolean isPending() {
        !executed
    }


    void handleErrorExecution(Exception e) {
        StringWriter stacktrace = new StringWriter()
        e.printStackTrace(new PrintWriter(stacktrace))
        addResult(RESULT_STACKTRACE_NAME, stacktrace.toString())
        addResult(RESULT_NAME, e.message)
        executionSuccessful = false
    }

    void handleSuccessfulExecution(String result) {
        addResult(RESULT_NAME, result)
        executionSuccessful = true
    }

}
