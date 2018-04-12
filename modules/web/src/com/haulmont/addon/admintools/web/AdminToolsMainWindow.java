package com.haulmont.addon.admintools.web;

import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import java.util.Map;

public class AdminToolsMainWindow extends AppMainWindow {

    public static final String GROOVY_CONSOLE_ENABLED_PROPERTY = "admintools.groovyConsole.enabled";
    public static final String SQL_CONSOLE_ENABLED_PROPERTY = "admintools.sqlConsole.enabled";
    public static final String JPQL_CONSOLE_ENABLED_PROPERTY = "admintools.jpqlConsole.enabled";
    public static final String DIAGNOSE_EXECUTION_LOG_ENABLED_PROPERTY = "admintools.diagnoseExecutionLog.enabled";

    public static final String SCRIPT_GENERATOR_ENABLED_PROPERTY = "admintools.scriptGenerator.enabled";
    public static final String SHELL_EXECUTOR_ENABLED_PROPERTY = "admintools.shellExecutor.enabled";
    public static final String SSH_TERMINAL_ENABLED_PROPERTY = "admintools.sshTerminal.enabled";
    public static final String CONFIG_LOADER_ENABLED_PROPERTY = "admintools.configLoader.enabled";
    public static final String CONSOLE_SCRIPT_LOADER_ENABLED_PROPERTY = "admintools.consoleScriptLoader.enabled";

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        mainMenu.getMenuItemNN("groovyConsole").setVisible(isShowMenuItem(GROOVY_CONSOLE_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("sqlConsole").setVisible(isShowMenuItem(SQL_CONSOLE_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("jpqlConsole").setVisible(isShowMenuItem(JPQL_CONSOLE_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("ddcrd$DiagnoseExecutionLog.browse").setVisible(isShowMenuItem(DIAGNOSE_EXECUTION_LOG_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("scriptGenerator").setVisible(isShowMenuItem(SCRIPT_GENERATOR_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("shellExecutor").setVisible(isShowMenuItem(SHELL_EXECUTOR_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("sshTerminal").setVisible(isShowMenuItem(SSH_TERMINAL_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("configLoader").setVisible(isShowMenuItem(CONFIG_LOADER_ENABLED_PROPERTY));
        mainMenu.getMenuItemNN("consoleScriptLoader").setVisible(isShowMenuItem(CONSOLE_SCRIPT_LOADER_ENABLED_PROPERTY));
    }

    protected boolean isShowMenuItem(String property){
        String value = AppContext.getProperty(property);
        return value == null ? true : Boolean.valueOf(value);
    }
}
