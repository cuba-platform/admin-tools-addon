package com.haulmont.addon.admintools.tomcat;

import com.haulmont.addon.admintools.console.ConsoleService;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authenticated;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;

@Component("tomcat_TomcatMBean")
public class Tomcat implements TomcatMBean {

    @Inject
    ConsoleService consoleService;

    @Inject
    Resources resources;

    String TOMCAT_DIR = AppContext.getProperty("catalina.home");
    String TIMEOUT_COMMAND = "timeout 60s ";

    @Authenticated
    @Override
    public void runShellScript(String prefix, String path, String name, String arguments) throws IOException {
        String absolutePath = StringUtils.isBlank(path) ? TOMCAT_DIR : TOMCAT_DIR + "/" + path;
        consoleService.executeShell(prefix, absolutePath, name, arguments);
    }

    @Authenticated
    @Override
    public void reboot() throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            consoleService.executeBat(getScript("restart.bat"), TOMCAT_DIR);
        } else if (SystemUtils.IS_OS_LINUX) {
            consoleService.executeShell(TIMEOUT_COMMAND, getScript("restart.sh"), TOMCAT_DIR + " <&- &");
        }
    }

    @Authenticated
    @Override
    public void shutdown() throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            consoleService.executeBat(getScript("shutdown.bat"), TOMCAT_DIR);
        } else if (SystemUtils.IS_OS_LINUX) {
            Runtime.getRuntime().exec(TIMEOUT_COMMAND + TOMCAT_DIR + "/bin/shutdown.bat");
        }
    }

    protected String getScript(String filename){
        // I don't understand why resources.getResource().getFile() return FileNotFoundException
        return resources.getResourceAsString("classpath:com/haulmont/addon/admintools/tomcat/"+ filename);
    }
}
