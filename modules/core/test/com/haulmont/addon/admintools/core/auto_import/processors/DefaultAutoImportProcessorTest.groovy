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

package com.haulmont.addon.admintools.core.auto_import.processors

import com.haulmont.addon.admintools.AdminToolsTestContainer
import com.haulmont.addon.admintools.core.auto_import.AutoImportException
import com.haulmont.addon.admintools.core.auto_import.processors.AutoImportProcessor
import com.haulmont.addon.admintools.core.auto_import.processors.DefaultAutoImportProcessor
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.LoadContext
import com.haulmont.cuba.security.entity.Group
import com.haulmont.cuba.security.entity.User
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class DefaultAutoImportProcessorTest extends Specification {

    @ClassRule
    @Shared
    AdminToolsTestContainer container = AdminToolsTestContainer.Common.INSTANCE

    AutoImportProcessor delegate
    DataManager dataManager

    final String ERROR_PATH = 'com/haulmont/addon/admintools/core/auto_import/processors/files/ErrorPath.json'
    final String FILE_WITH_ERROR_EXTENSION = 'com/haulmont/addon/admintools/core/auto_import/processors/files/ErrorExtension.txt'
    final String INCORRECT_JSON = 'com/haulmont/addon/admintools/core/auto_import/processors/files/Incorrect.json'
    final String CORRECT_JSON = 'com/haulmont/addon/admintools/core/auto_import/processors/files/Group.json'
    final String CORRECT_ZIP = 'com/haulmont/addon/admintools/core/auto_import/processors/files/GroupAndUsers.zip'

    void setup() {
        delegate = AppBeans.get(DefaultAutoImportProcessor)
        dataManager = AppBeans.get(DataManager)
    }

    def "try import file by error path"() {
        when:
        delegate.processFile(ERROR_PATH)

        then:
        thrown(FileNotFoundException)
    }

    def "try import file with error extension"() {
        when:
        delegate.processFile(FILE_WITH_ERROR_EXTENSION)

        then:
        AutoImportException ex = thrown(AutoImportException)
        ex.message == "File type is not supported: txt"
    }

    def "try import incorrect Json file"() {
        when:
        delegate.processFile(INCORRECT_JSON)

        then:
        thrown(RuntimeException)
    }

    def "import correct Json file. Group should be in DB"() {
        given:
        String groupId = '9162f700-ba68-92c8-160d-7330603a7b44'
        LoadContext<Group> groupLoadContext = LoadContext
                .create(Group.class)
                .setQuery(LoadContext.createQuery('select g from sec$Group g'))
                .setView('_local')

        when:
        delegate.processFile(CORRECT_JSON)

        then:
        List<Group> groups = dataManager.loadList(groupLoadContext)
        groups.stream().anyMatch({ g -> g.id.toString() == groupId })
    }

    def "import correct zip. Group and users should be in db"() {
        given:
        LoadContext<User> usersLoadContext = LoadContext
                .create(User.class)
                .setQuery(
                LoadContext.createQuery('select u from sec$User u where u.id in :ids')
                        .setParameter('ids', ['a46ce38f-bed9-f0c2-5972-ad067e9f8275', 'b83180fe-7ef7-ec74-af1d-b508b6bd22f2']))
                .setView('user.edit')

        def isDbConstrainsUsersFromJson = { users -> users.stream().map({ user -> user.login }).collect().containsAll(['User1', 'User2']) }

        when:
        delegate.processFile(CORRECT_ZIP)

        then:
        List<User> users = dataManager.loadList(usersLoadContext)
        isDbConstrainsUsersFromJson(users)
        users.get(0).group.id.toString() == '9162f700-ba68-92c8-160d-7330603a7b44'
        users.get(1).group.id.toString() == '9162f700-ba68-92c8-160d-7330603a7b44'
    }
}
