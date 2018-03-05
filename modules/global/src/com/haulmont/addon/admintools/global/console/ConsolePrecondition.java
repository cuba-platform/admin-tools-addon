package com.haulmont.addon.admintools.global.console;

import org.springframework.stereotype.Component;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

@Component("admintools_ConsolePrecondition")
public class ConsolePrecondition {

    public boolean isOsUnix() {
        return IS_OS_LINUX || IS_OS_MAC || IS_OS_MAC_OSX;
    }

    public boolean isOsWindows() {
        return IS_OS_WINDOWS;
    }
}
