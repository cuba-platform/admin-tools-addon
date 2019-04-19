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

package com.haulmont.addon.admintools.global.console

import spock.lang.Specification

class ConsoleToolsTest extends Specification {
    def consoleToolsUnix
    def consoleToolsWindows

    void setup() {
        consoleToolsUnix = new ConsoleToolsUnix()
        consoleToolsWindows = new ConsoleToolsWindows()
    }

    def "parse arguments in Unix OS"() {
        when:
        List<String> parsedArgs = consoleToolsUnix.parseArgs(argument)

        then:
        parsedArgs.containsAll(result)

        where:
        argument          | result
        ''                | []
        'arg'             | ['arg']
        'two arg'         | ['two', 'arg']
        '"one one" two'   | ['one one', 'two']
        '\'one one\' two' | ['one one', 'two']
        'one\\ one two'   | ['one one', 'two']
    }

    def "parse arguments in Windows OS"() {
        when:
        List<String> parsedArgs = consoleToolsWindows.parseArgs(argument)

        then:
        parsedArgs.containsAll(result)

        where:
        argument          | result
        ''                | []
        'arg'             | ['arg']
        'two arg'         | ['two', 'arg']
    }

    class ConsoleToolsUnix extends ConsoleTools {
        @Override
        boolean isOsUnix() {
            return true
        }

        @Override
        boolean isOsWindows() {
            return false
        }
    }

    class ConsoleToolsWindows extends ConsoleTools {
        @Override
        boolean isOsUnix() {
            return false
        }

        @Override
        boolean isOsWindows() {
            return true
        }
    }
}
