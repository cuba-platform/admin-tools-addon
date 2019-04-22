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

import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.runtimediagnose.wizard.DiagnoseWizardResult
import de.diedavids.cuba.runtimediagnose.wizard.DiagnoseWizardResultType
import groovy.transform.CompileStatic
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service(DiagnoseWizardResultService.NAME)
@CompileStatic
class DiagnoseWizardResultServiceBean implements DiagnoseWizardResultService {

    @Inject
    Metadata metadata

    @Inject
    Messages messages

    @Override
    Collection<DiagnoseWizardResult> createResultsForDiagnose(DiagnoseExecution diagnose) {
        [
            createBasicSuccessErrorResult(diagnose)
        ]
    }

    private DiagnoseWizardResult createBasicSuccessErrorResult(DiagnoseExecution diagnose) {
        DiagnoseWizardResult wizardResult = createDiagnoseFileValidation()

        if (diagnose.executionSuccessful) {
            wizardResult.type = DiagnoseWizardResultType.SUCCESS

            wizardResult.message = messages.getMessage(getClass(), 'diagnoseExecutedSuccessful')
        }
        else {
            wizardResult.type = DiagnoseWizardResultType.ERROR
            wizardResult.message = messages.getMessage(getClass(), 'diagnoseExecutedError')
        }
        wizardResult
    }

    private DiagnoseWizardResult createDiagnoseFileValidation() {
        metadata.create(DiagnoseWizardResult)
    }

}