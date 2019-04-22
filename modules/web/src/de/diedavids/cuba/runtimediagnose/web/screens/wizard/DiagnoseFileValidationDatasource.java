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

package de.diedavids.cuba.runtimediagnose.web.screens.wizard;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecution;
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseFileValidationService;
import de.diedavids.cuba.runtimediagnose.wizard.DiagnoseWizardResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class DiagnoseFileValidationDatasource extends CustomCollectionDatasource<DiagnoseWizardResult, UUID> {

    private DiagnoseFileValidationService diagnoseFileValidationService = getDiagnoseFileValidationService();

    protected DiagnoseFileValidationService getDiagnoseFileValidationService() {
        return AppBeans.get(DiagnoseFileValidationService.NAME);
    }

    @Override
    protected Collection<DiagnoseWizardResult> getEntities(Map params) {

        DiagnoseExecution diagnose = (DiagnoseExecution) params.get("diagnose");

        if (diagnose != null) {
            return diagnoseFileValidationService.validateDiagnose(diagnose);
        } else {
            return new HashSet<>();
        }

    }
}