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

package com.haulmont.addon.admintools.core.script_generator

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.global.script_generator.EntityViewSqlGenerationService
import com.haulmont.addon.admintools.global.script_generator.ScriptGenerationOptions
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.LoadContext
import com.haulmont.cuba.security.entity.User
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class EntityViewSqlGenerationServiceTest extends Specification {
    @ClassRule
    @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    EntityViewSqlGenerationService delegate
    DataManager dataManager
    User localAdmin
    User editAdmin

    def localView = '_local'
    def editView = 'user.edit'
    String[] localViewColumns = ['ID', 'VERSION', 'CREATE_TS', 'CREATED_BY', 'UPDATE_TS', 'UPDATED_BY', 'DELETE_TS',
                                 'DELETED_BY', 'LOGIN', 'LOGIN_LC', 'PASSWORD', 'NAME', 'FIRST_NAME', 'LAST_NAME',
                                 'MIDDLE_NAME', 'POSITION_', 'EMAIL', 'LANGUAGE_', 'TIME_ZONE', 'TIME_ZONE_AUTO',
                                 'ACTIVE', 'CHANGE_PASSWORD_AT_LOGON', 'GROUP_ID', 'IP_MASK']


    void setup() {
        delegate = AppBeans.get(EntityViewSqlGenerationService)
        dataManager = AppBeans.get(DataManager)

        LoadContext<User> adminLocal = LoadContext.create(User.class)
                .setQuery(LoadContext.createQuery('select u from sec$User u where u.login = \'admin\''))
                .setView(localView)
        localAdmin = dataManager.load(adminLocal)
        editAdmin = dataManager.load(adminLocal.setView(editView))

    }

    def "check generate insert scripts for user 'admin' with view '_local'"() {
        when:
        Set<String> scripts = delegate.generateScript(localAdmin, localView, ScriptGenerationOptions.INSERT)

        then:
        scripts.size() == 1
        String script = scripts.getAt(0)
        script.startsWith('insert into SEC_USER \n')
        containsAllWords(script, localViewColumns)
        containsAllWords(script, 'values', 'admin', 'Administrator')
    }

    def "check generate insert scripts for user 'admin' with view 'user.edit'"() {
        when:
        Set<String> scripts = delegate.generateScript(editAdmin, editView, ScriptGenerationOptions.INSERT)

        then:
        scripts.size() == 4

        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_ROLE') })
        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_GROUP') })
        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_USER_ROLE') })

        scripts.stream().anyMatch({ s ->
            s.startsWith('insert into SEC_USER') &&
                    containsAllWords(s, localViewColumns) &&
                    containsAllWords(s, 'values', 'admin', 'Administrator')
        })
    }

    def "check generate update scripts for user 'admin' with view '_local'"() {
        when:
        Set<String> scripts = delegate.generateScript(localAdmin, localView, ScriptGenerationOptions.UPDATE)


        then:
        scripts.size() == 1
        String script = scripts.getAt(0)
        script.startsWith('update SEC_USER')
        containsAllWords(script, localViewColumns)
        containsAllWords(script, 'set', 'admin', 'Administrator')
    }

    def "check generate update scripts for user 'admin' with view 'user.edit'"() {
        when:
        Set<String> scripts = delegate.generateScript(editAdmin, editView, ScriptGenerationOptions.UPDATE)

        then:
        scripts.size() == 4

        scripts.stream().anyMatch({ s -> s.startsWith('update SEC_ROLE') })
        scripts.stream().anyMatch({ s -> s.startsWith('update SEC_GROUP') })
        scripts.stream().anyMatch({ s -> s.startsWith('update SEC_USER_ROLE') })


        scripts.stream().anyMatch({ s ->
            s.startsWith('update SEC_USER') &&
                    containsAllWords(s, localViewColumns) &&
                    containsAllWords(s, 'set', 'admin', 'Administrator')
        })

    }

    def "check generate insert and update scripts for user 'admin' with view '_local'"() {
        when:
        Set<String> scripts = delegate.generateScript(localAdmin, localView, ScriptGenerationOptions.INSERT_UPDATE)


        then:
        scripts.size() == 1
        String script = scripts.getAt(0)
        containsAllWords(script, 'insert into SEC_USER', 'update SEC_USER', 'value', 'set', 'admin', 'Administrator')
        containsAllWords(script, localViewColumns)
    }

    def "check generate insert and update scripts for user 'admin' with view 'user.edit'"() {
        when:
        Set<String> scripts = delegate.generateScript(editAdmin, editView, ScriptGenerationOptions.INSERT_UPDATE)

        then:
        scripts.size() == 4

        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_ROLE') && s.contains('update SEC_ROLE') })
        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_GROUP') && s.contains('update SEC_GROUP') })
        scripts.stream().anyMatch({ s -> s.startsWith('insert into SEC_USER_ROLE') && s.contains('update SEC_USER_ROLE') })

        scripts.stream().anyMatch({ s ->
            s.startsWith('insert into SEC_USER') &&
                    containsAllWords(s, localViewColumns) &&
                    containsAllWords(s, 'update SEC_USER', 'set', 'admin', 'Administrator')
        })

    }

    static boolean containsAllWords(String word, String... keywords) {
        for (String k : keywords) {
            if (!word.contains(k)) {
                return false
            }
        }
        return true
    }
}
