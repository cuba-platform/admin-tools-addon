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

package de.diedavids.cuba.runtimediagnose.groovy.binding

import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.TimeSource
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject

@Slf4j
@Component('ddcrd_DefaultGroovyScriptTeststepBindingSupplier')
class DefaultGroovyScriptBindingSupplier implements GroovyScriptBindingSupplier {

    @Inject
    Persistence persistence

    @Inject
    DataManager dataManager

    @Inject
    TimeSource timeSource

    @Inject
    Metadata metadata

    @Override
    Map<String, Object> getBinding() {
        [
                dataManager: dataManager,
                persistence: persistence,
                metadata   : metadata,
                bean       : beanClosure,
                getSql     : sqlClosure,
        ]
    }

    protected Closure getBeanClosure() {
        return { String name ->
            AppBeans.get(name)
        }

    }

    protected Closure getSqlClosure() {
        return { String name = null ->
            def dataSource = name ? persistence.getDataSource(name) : persistence.dataSource
            new Sql(dataSource)
        }
    }

}