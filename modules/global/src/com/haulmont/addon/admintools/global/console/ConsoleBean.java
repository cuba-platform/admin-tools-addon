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

package com.haulmont.addon.admintools.global.console;

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

/**
 * This class delegate executing scripts to the associated OS
 */
@Component("admintools_ConsoleBean")
public class ConsoleBean {

    @Inject
    protected ConsoleTools consoleTools;

    /**
     * Generate temp file with text {@code script} and try execute it in a terminal.
     *
     * @param script is text of needed script
     * @param arguments emulates args in command console
     * @return process of executing script
     * @throws IOException  if an I/O error occurs
     */
    public Process execute(String script, List<String> arguments) throws IOException {
        return internalExecute(generateFile(script), arguments);
    }

    /**
     * Try execute {@code script} in a terminal
     *
     * @param script script to execute
     * @param arguments emulates args in command console
     * @return process of executing script
     * @throws IOException  if an I/O error occurs
     */
    public Process execute(File script, List<String> arguments) throws IOException {
        return internalExecute(script, arguments);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected File generateFile(String script) throws IOException {
        String extension = "";

        if (consoleTools.isOsWindows()) {
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
        if (consoleTools.isOsUnix()) {
            commands.add("./" + scriptName);
        } else if (consoleTools.isOsWindows()) {
            commands.addAll(Arrays.asList("cmd", "/c", "start", scriptName));
        } else {
            commands.add(scriptName); // we doesn't care about user if it uses esoteric OS
        }

        commands.addAll(arguments);

        return commands;
    }
}
