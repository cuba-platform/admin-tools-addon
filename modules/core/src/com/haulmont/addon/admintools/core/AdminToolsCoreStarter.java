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

package com.haulmont.addon.admintools.core;

import com.haulmont.addon.admintools.core.auto_import.AutoImporter;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.events.AppContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * This class runs inner components, when the application is started
 */
@Component("admintools_AdminToolsCoreStarter")
public class AdminToolsCoreStarter {

    public static final String AUTO_IMPORT_ENABLED_PROPERTY = "admintools.autoImport.enabled";

    @Inject
    protected AutoImporter autoImporter;

    @EventListener
    public void applicationStarted(AppContextStartedEvent event) {
        String autoImportEnabled = AppContext.getProperty(AUTO_IMPORT_ENABLED_PROPERTY);

        if (Boolean.valueOf(autoImportEnabled)) {
            autoImporter.startImport();
        }
    }
}
