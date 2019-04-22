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

package de.diedavids.cuba.runtimediagnose.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s -  %s: %s|executionTimestamp,executionUser,executionSuccessful")
@Table(name = "DDCRD_DIAGNOSE_EXECUTION_LOG")
@Entity(name = "ddcrd$DiagnoseExecutionLog")
public class DiagnoseExecutionLog extends StandardEntity {
    private static final long serialVersionUID = -6159151186984209802L;

    @NotNull
    @Column(name = "EXECUTION_SUCCESSFUL", nullable = false)
    protected Boolean executionSuccessful = false;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "EXECUTION_TIMESTAMP", nullable = false)
    protected Date executionTimestamp;

    @Column(name = "EXECUTION_USER")
    protected String executionUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXECUTION_RESULT_FILE_ID")
    protected FileDescriptor executionResultFile;

    @NotNull
    @Column(name = "DIAGNOSE_TYPE", nullable = false)
    protected String diagnoseType;

    @NotNull
    @Column(name = "EXECUTION_TYPE", nullable = false)
    protected String executionType;

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getExecutionType() {
        return executionType;
    }


    public void setDiagnoseType(String diagnoseType) {
        this.diagnoseType = diagnoseType;
    }

    public String getDiagnoseType() {
        return diagnoseType;
    }


    public void setExecutionResultFile(FileDescriptor executionResultFile) {
        this.executionResultFile = executionResultFile;
    }

    public FileDescriptor getExecutionResultFile() {
        return executionResultFile;
    }


    public void setExecutionSuccessful(Boolean executionSuccessful) {
        this.executionSuccessful = executionSuccessful;
    }

    public Boolean getExecutionSuccessful() {
        return executionSuccessful;
    }

    public void setExecutionTimestamp(Date executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }

    public Date getExecutionTimestamp() {
        return executionTimestamp;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getExecutionUser() {
        return executionUser;
    }


}