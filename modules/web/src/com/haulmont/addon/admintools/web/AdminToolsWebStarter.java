package com.haulmont.addon.admintools.web;

import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.config.MenuConfig;
import com.haulmont.cuba.gui.config.MenuItem;
import com.haulmont.cuba.web.security.events.AppStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("admintools_AdminToolsWebStarter")
public class AdminToolsWebStarter {

    public static final String GROOVY_CONSOLE_ENABLED_PROPERTY = "admintools.groovyConsole.enabled";
    public static final String SQL_CONSOLE_ENABLED_PROPERTY = "admintools.sqlConsole.enabled";
    public static final String JPQL_CONSOLE_ENABLED_PROPERTY = "admintools.jpqlConsole.enabled";
    public static final String DIAGNOSE_EXECUTION_LOG_ENABLED_PROPERTY = "admintools.diagnoseExecutionLog.enabled";

    public static final String SCRIPT_GENERATOR_ENABLED_PROPERTY = "admintools.scriptGenerator.enabled";
    public static final String SHELL_EXECUTOR_ENABLED_PROPERTY = "admintools.shellExecutor.enabled";
    public static final String SSH_TERMINAL_ENABLED_PROPERTY = "admintools.sshTerminal.enabled";
    public static final String CONFIG_LOADER_ENABLED_PROPERTY = "admintools.configLoader.enabled";
    public static final String CONSOLE_SCRIPT_LOADER_ENABLED_PROPERTY = "admintools.consoleScriptLoader.enabled";


    @EventListener
    public void applicationStarted(AppStartedEvent event) {
        MenuConfig menuConfig = (MenuConfig) AppContext.getApplicationContext().getBean(MenuConfig.NAME);

        Optional<MenuItem> administrationItemOpt = menuConfig.getRootItems().stream()
                .filter(menuItem -> "administration".equals(menuItem.getId()))
                .findFirst();

        if (administrationItemOpt.isPresent()) {
            for (MenuItem menuItem : administrationItemOpt.get().getChildren()) {
                if ("consoleMenu".equals(menuItem.getId())) {
                    disableMenus(menuItem, getConsoleProperties());
                } else if ("adminTools".equals(menuItem.getId())) {
                    disableMenus(menuItem, getAdminToolsProperties());
                }
            }
        }
    }

    protected void disableMenus(MenuItem consoleMenu, Map<String, String> properties) {
        List<MenuItem> children = consoleMenu.getChildren();
        List<MenuItem> removedItems = new ArrayList<>();

        for (MenuItem item : children) {
            for (Map.Entry<String, String> prop : properties.entrySet()) {
                if (item.getId().equals(prop.getKey()) && isRemovedMenuItem(prop.getValue())) {
                    removedItems.add(item);
                }
            }
        }

        children.removeAll(removedItems);
    }

    protected boolean isRemovedMenuItem(String property) {
        String value = AppContext.getProperty(property);
        return value != null && !Boolean.valueOf(property);
    }

    protected Map<String, String> getConsoleProperties() {
        return new HashMap<String, String>() {{
            put("groovyConsole", GROOVY_CONSOLE_ENABLED_PROPERTY);
            put("sqlConsole", SQL_CONSOLE_ENABLED_PROPERTY);
            put("jpqlConsole", JPQL_CONSOLE_ENABLED_PROPERTY);
            put("ddcrd$DiagnoseExecutionLog.browse", DIAGNOSE_EXECUTION_LOG_ENABLED_PROPERTY);
        }};
    }

    protected Map<String, String> getAdminToolsProperties() {
        return new HashMap<String, String>() {{
            put("scriptGenerator", SCRIPT_GENERATOR_ENABLED_PROPERTY);
            put("shellExecutor", SHELL_EXECUTOR_ENABLED_PROPERTY);
            put("sshTerminal", SSH_TERMINAL_ENABLED_PROPERTY);
            put("configLoader", CONFIG_LOADER_ENABLED_PROPERTY);
            put("consoleScriptLoader", CONSOLE_SCRIPT_LOADER_ENABLED_PROPERTY);
        }};
    }
}
