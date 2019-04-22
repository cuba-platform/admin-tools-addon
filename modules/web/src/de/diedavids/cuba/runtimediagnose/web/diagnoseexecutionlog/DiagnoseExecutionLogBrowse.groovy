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

package de.diedavids.cuba.runtimediagnose.web.diagnoseexecutionlog

import com.haulmont.cuba.core.app.FileStorageService
import com.haulmont.cuba.gui.components.AbstractLookup
import com.haulmont.cuba.gui.components.GroupTable
import de.diedavids.cuba.runtimediagnose.entity.DiagnoseExecutionLog
import de.diedavids.cuba.runtimediagnose.web.screens.diagnose.DiagnoseFileDownloader

import javax.inject.Inject

class DiagnoseExecutionLogBrowse extends AbstractLookup {

    @Inject
    GroupTable<DiagnoseExecutionLog> diagnoseExecutionLogsTable

    @Inject
    DiagnoseFileDownloader diagnoseFileDownloader

    @Inject
    FileStorageService fileStorageService

    void downloadResultFile() {
        DiagnoseExecutionLog executionLog = diagnoseExecutionLogsTable.singleSelected
        def zipBytes = fileStorageService.loadFile(executionLog.executionResultFile)
        diagnoseFileDownloader.downloadFile(this,zipBytes, executionLog.executionResultFile.name)
    }
}