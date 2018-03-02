package com.haulmont.addon.admintools.tomcat;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;

@ManagedResource(description = "Operations for Tomcat container")
public interface TomcatMBean {

    @ManagedOperation(description = "Execute script by path in tomcat directory")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "path", description = "Relative script path from tomcat dir"),
            @ManagedOperationParameter(name = "arguments", description = "Arguments for a script")
    })
    void executeScript(String relativePath, String arguments) throws IOException;

    @ManagedOperation(description = "Reboot tomcat in the core module")
    void reboot() throws IOException, InterruptedException;

    @ManagedOperation(description = "Shutdown tomcat in the core module")
    void shutdown() throws IOException, InterruptedException;

    @ManagedOperation(description = "Return an absolute path to the tomcat directory")
    String getTomcatAbsolutePath();
}
