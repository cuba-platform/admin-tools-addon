package com.haulmont.addon.admintools.tomcat;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;

@ManagedResource(description = "Operations for TomCat container")
public interface TomcatMBean {

    @ManagedOperation(description = "Run shell script by path in tomcat directory")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "prefix", description = "Prefix for script"),
            @ManagedOperationParameter(name = "path", description = "Relative path from tomcat dir"),
            @ManagedOperationParameter(name = "name", description = "Script name"),
            @ManagedOperationParameter(name = "arguments", description = "Arguments for a script")
    })
    void runShellScript(String prefix, String path, String name, String arguments) throws IOException;

    @ManagedOperation(description = "Reboot TomCat in the core module")
    void reboot() throws IOException;

    @ManagedOperation(description = "Shutdown TomCat in the core module")
    void shutdown() throws IOException;
}
