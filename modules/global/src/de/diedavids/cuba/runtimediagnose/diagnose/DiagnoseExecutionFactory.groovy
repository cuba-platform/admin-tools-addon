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

interface DiagnoseExecutionFactory {

    public static final String NAME = 'ddcrd_DiagnoseExecutionFactory'

    /**
     * creates a diagnose execution from a File
     * @param file the file to read from (zip file)
     * @return the parsed diagnose execution information
     */
    DiagnoseExecution createDiagnoseExecutionFromFile(File file)

    /**
     * creates an ad-hoc diagnose execution
     * @param diagnoseScript the diagnose script to use
     * @param diagnoseType the diagnose type to use
     * @return the diagnose execution instance
     */
    DiagnoseExecution createAdHocDiagnoseExecution(String diagnoseScript, DiagnoseType diagnoseType)


    /**
     * creates an execution result (as zip bytes) form a given diagnose exection
     * @param diagnoseExecution the diagnose execution to create a zip file from
     * @return the execution result (as zip bytes)
     */
    byte[] createExecutionResultFromDiagnoseExecution(DiagnoseExecution diagnoseExecution)

    /**
     * creates a diagnose request file (as zip bytes) form a given diagnose exection
     * @param diagnoseExecution the diagnose execution to create a zip file from
     * @return the diagnose request (as zip bytes)
     */
    byte[] createDiagnoseRequestFileFromDiagnoseExecution(DiagnoseExecution diagnoseExecution)


}
