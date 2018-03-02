package com.haulmont.addon.admintools.console;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getName;

@Component("admintools_ConsoleBean")
public class ConsoleBean {

    @Inject
    protected ConsolePrecondition precondition;

    public Process execute(String script, List<String> arguments) throws IOException {
        return internalExecute(generateFile(script), arguments);
    }

    public Process execute(File script, List<String> arguments) throws IOException {
        return internalExecute(script, arguments);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected File generateFile(String script) throws IOException {
        String extension = "";

        if (precondition.isOsWindows()) {
            extension = ".bat";
        }

        File tempFile = File.createTempFile("script", extension);
        tempFile.setExecutable(true);
        tempFile.setWritable(true);
        tempFile.setReadable(true);
        tempFile.deleteOnExit();

        try (Writer writer = new FileWriter(tempFile)) {
            writer.write(script);
        }

        return tempFile;
    }

    protected Process internalExecute(File script, List<String> arguments) throws IOException {
        List<String> buildCommand = buildCommand(script, arguments);

        ProcessBuilder pb = new ProcessBuilder(buildCommand);
        pb.directory(script.getParentFile());
        pb.redirectErrorStream(true);
        return pb.start();
    }

    /**
     * Builds a command ready to execute
     *
     * @param script script will be executed
     * @param arguments arguments
     * @throws UnsupportedOperationException if host os is not supported by tool
     * @return built command
     */
    protected List<String> buildCommand(File script, List<String> arguments) {
        String scriptName = getName(script.getAbsolutePath());

        List<String> commands = new ArrayList<>();
        if (precondition.isOsUnix()) {
            commands.add("./" + scriptName);
        } else if (precondition.isOsWindows()) {
            commands.addAll(Arrays.asList("cmd", "/c", "start", scriptName));
        } else {
            commands.add(scriptName); // we doesn't care about user if it uses esoteric OS
        }

        commands.addAll(arguments);

        return commands;
    }
}
