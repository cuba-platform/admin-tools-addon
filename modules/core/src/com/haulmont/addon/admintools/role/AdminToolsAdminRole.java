/*
 * Copyright (c) 2008-2020 Haulmont.
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

package com.haulmont.addon.admintools.role;

import com.haulmont.addon.admintools.global.ssh.SshCredentials;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.EntityAccess;
import com.haulmont.cuba.security.app.role.annotation.EntityAttributeAccess;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.ScreenAccess;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;
import de.diedavids.cuba.runtimediagnose.entity.DiagnoseExecutionLog;
import de.diedavids.cuba.runtimediagnose.wizard.DiagnoseWizardResult;

@Role(name = "admin-tools-full-access")
public class AdminToolsAdminRole extends AnnotatedRoleDefinition {

    @Override
    public String getLocName() {
        return "Admin tools full access";
    }

    @EntityAccess(entityClass = DiagnoseWizardResult.class, operations = {EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ, EntityOp.DELETE})
    @EntityAccess(entityClass = DiagnoseExecutionLog.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = FileDescriptor.class, operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE})
    @EntityAccess(entityClass = SshCredentials.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = DiagnoseWizardResult.class, modify = "*")
    @EntityAttributeAccess(entityClass = DiagnoseExecutionLog.class, modify = "*")
    @EntityAttributeAccess(entityClass = SshCredentials.class, modify = "*")
    @EntityAttributeAccess(entityClass = FileDescriptor.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {"administration", "consoleMenu", "adminTools", "groovyConsole", "sqlConsole", "jpqlConsole", "diagnoseWizard", "ddcrd$DiagnoseExecutionLog.browse", "scriptGenerator", "shellExecutor", "sshTerminal", "configLoader", "consoleScriptLoader", "sqlCopyDialog", "shell-executor", "ssh-terminal", "console-script-loader", "console-frame", "admintools$configLoader", "admintools$scriptGeneratorsDialog", "admintools$scriptGeneratorsResult"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }
}
