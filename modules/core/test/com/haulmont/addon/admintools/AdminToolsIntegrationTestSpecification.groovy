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

package com.haulmont.addon.admintools

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class AdminToolsIntegrationTestSpecification extends Specification {

    @ClassRule @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    Metadata metadata
    DataManager dataManager

    void setup() {
        metadata = AppBeans.get(Metadata)
        dataManager = AppBeans.get(DataManager)
    }

//    void cleanup() {
//        builder.cleanUp()
//    }

}
