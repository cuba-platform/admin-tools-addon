package com.haulmont.addon.admintools.core.tomcat;

import com.haulmont.addon.admintools.global.console.ConsoleBean;
import com.haulmont.addon.admintools.global.console.ConsoleTool;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authenticated;
import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.SystemUtils.*;

@Component("cuba-at_TomcatMBean")
public class Tomcat implements TomcatMBean {

    @Inject
    protected ConsoleBean consoleBean;
    @Inject
    protected ConsoleTool consoleTool;

    @Inject
    protected Resources resources;

    protected String SCRIPTS_FOLDER = "com/haulmont/addon/admintools/console-scripts/";
    protected String TOMCAT_DIR = AppContext.getProperty("catalina.home");

    @Authenticated
    @Override
    public void executeScript(String relativePath, String arguments) throws IOException {
        if (isBlank(relativePath)) {
            throw new IllegalArgumentException("Relative path can't be empty");
        }

        File script = Paths.get(TOMCAT_DIR, relativePath).toFile();
        List<String> parsedArgs = consoleTool.parseArgs(arguments != null ? arguments : "");
        consoleBean.execute(script, parsedArgs);
    }

    @Authenticated
    @Override
    public void reboot() throws IOException, InterruptedException {
        if (SystemUtils.IS_OS_WINDOWS) {
            executeBat("restart.bat");
        } else if (IS_OS_LINUX || IS_OS_MAC || IS_OS_MAC_OSX) {
            String script = getScript("restart.sh");
            List<String> parsedArgs = consoleTool.parseArgs(format("\"%s\" <&- &", TOMCAT_DIR));
            consoleBean.execute(script, parsedArgs).waitFor(60, SECONDS);
        }
    }

    @Authenticated
    @Override
    public void shutdown() throws IOException, InterruptedException {
        if (SystemUtils.IS_OS_WINDOWS) {
            executeBat("shutdown.bat");
        } else if (IS_OS_LINUX || IS_OS_MAC || IS_OS_MAC_OSX) {
            File script = Paths.get(TOMCAT_DIR, "/bin/shutdown.sh").toFile();
            consoleBean.execute(script, emptyList()).waitFor(60, SECONDS);
        }
    }

    @Authenticated
    @Override
    public String getTomcatAbsolutePath() {
        return TOMCAT_DIR;
    }

    @Nonnull
    protected String getScript(String filename) throws FileNotFoundException {
        // resources.getResource().getFile() can't return file from jar
        String script = resources.getResourceAsString("classpath:" + SCRIPTS_FOLDER + filename);

        if (script == null) {
            throw new FileNotFoundException(format("Script %s is not found", filename));
        }
        return script;
    }

    protected void executeBat(String scriptName) throws IOException, InterruptedException {
        String script = getScript(scriptName);
        consoleBean.execute(script, singletonList(TOMCAT_DIR)).waitFor(60, SECONDS);
    }

}
