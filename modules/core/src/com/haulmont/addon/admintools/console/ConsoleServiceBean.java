package com.haulmont.addon.admintools.console;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service(ConsoleService.NAME)
public class ConsoleServiceBean implements ConsoleService {

    String BAT = ".bat";
    String SH = ".sh";
    String CMD = "cmd /c start";

    @Override
    public String executeShell(String prefix, String absolutePath, String scriptName, String arguments) throws IOException {
        checkLinuxOS();
        return executeScript(prefix, absolutePath, scriptName, arguments, SH);
    }


    @Override
    public String executeShell(String prefix, String script, String arguments) throws IOException {
        checkLinuxOS();
        return executeCommand(prefix, script, arguments, SH);
    }

    @Override
    public String executeBat(String absolutePath, String scriptName, String arguments) throws IOException {
        checkWindowsOS();
        return executeScript(CMD, absolutePath, scriptName, arguments, BAT);
    }

    @Override
    public String executeBat(String script, String arguments) throws IOException {
        checkWindowsOS();
        return executeCommand(CMD, script, arguments, BAT);
    }

    String executeCommand(String prefix, String script, String arguments, String suffix) throws IOException {
        checkScriptIsEmpty(script);
        String shebangScript = addShebang(script);

        File tempFile = File.createTempFile("script", suffix);
        tempFile.setExecutable(true);
        tempFile.setWritable(true);
        tempFile.setReadable(true);
        Files.write(Paths.get(tempFile.getPath()), shebangScript.getBytes());

        return executeScript(prefix, tempFile, arguments);
    }

    String executeScript(String prefix, String absolutePath, String scriptName, String arguments, String suffix) throws IOException {
        checkPathAndName(absolutePath, scriptName);

        File script = new File(absolutePath + "/" + scriptName + suffix);
        checkScriptIsExist(script);

        return executeScript(prefix, script, arguments);
    }

    String executeScript(String prefix, File script, String arguments) throws IOException {
        Process p = Runtime.getRuntime().exec(prefix + " " + script.getAbsolutePath() + " " + arguments);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("this should not happen", e);
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        return output.toString();
    }

    void checkScriptIsEmpty(String script) {
        if (StringUtils.isBlank(script)) {
            throw new IllegalArgumentException("Script can't be empty");
        }
    }

    void checkLinuxOS() {
        if (!SystemUtils.IS_OS_LINUX) {
            throw new UnsupportedOperationException("This operation is not supported for not Linux OS");
        }
    }

    void checkWindowsOS() {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new UnsupportedOperationException("This operation is not supported for not Windows OS");
        }
    }

    void checkScriptIsExist(File script) throws FileNotFoundException {
        if (!script.exists() || script.isDirectory()) {
            throw new FileNotFoundException("Script not found by path: " + script.getAbsolutePath());
        }
    }

    void checkPathAndName(String absolutePath, String scriptName) {
        if (StringUtils.isBlank(absolutePath)) {
            throw new IllegalArgumentException("Path can't be empty");
        }
        if (StringUtils.isBlank(scriptName)) {
            throw new IllegalArgumentException("Script name can't be empty");
        }
    }

    String addShebang(String script) {
        if (!script.startsWith("#!/usr/bin/env bash")) {
            return "#!/usr/bin/env bash\n" + script;
        }

        return script;
    }
}
