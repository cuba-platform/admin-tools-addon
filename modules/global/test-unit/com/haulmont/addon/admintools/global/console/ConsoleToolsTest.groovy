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
