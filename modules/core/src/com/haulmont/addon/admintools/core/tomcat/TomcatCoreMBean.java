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

package com.haulmont.addon.admintools.core.tomcat;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;

@ManagedResource(description = "Operations on Tomcat running the core block")
public interface TomcatCoreMBean {

    @ManagedOperation(description = "Execute script by path in Tomcat directory")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "path", description = "Relative script path from Tomcat dir"),
            @ManagedOperationParameter(name = "arguments", description = "Arguments for a script")
    })
    void executeScript(String relativePath, String arguments) throws IOException;

    @ManagedOperation(description = "Reboot Tomcat in the core module")
    void reboot() throws IOException, InterruptedException;

    @ManagedOperation(description = "Shutdown Tomcat in the core module")
    void shutdown() throws IOException, InterruptedException;

    @ManagedOperation(description = "Return an absolute path to the Tomcat directory")
    String getTomcatAbsolutePath();
}
