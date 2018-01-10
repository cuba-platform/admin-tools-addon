package com.haulmont.addon.admintools.console;

import java.io.IOException;

public interface ConsoleService {

    String NAME = "admintools_ConsoleService";

    String executeShell(String prefix, String absolutePath, String scriptName, String arguments) throws IOException;

    String executeShell(String prefix, String script, String arguments) throws IOException;

    String executeBat(String absolutePath, String scriptName, String arguments) throws IOException;

    String executeBat(String script, String arguments) throws IOException;

}
