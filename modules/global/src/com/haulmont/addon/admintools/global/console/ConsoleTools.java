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

package com.haulmont.addon.admintools.global.console;

import org.springframework.stereotype.Component;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.text.CharacterIterator.DONE;
import static org.apache.commons.lang3.SystemUtils.*;

/**
 * Utility class to provide functionality related to {@link ConsoleBean}
 */
@Component("admintools_ConsoleTool")
public class ConsoleTools {

    /**
     * Tried parse arguments {@code line} like in a command console of a current OS
     * @return prepared arguments for {@link ProcessBuilder}
     */
    public List<String> parseArgs(String line) {
        List<String> args = new ArrayList<>();

        if (isOsUnix()) {
            StringCharacterIterator iterator = new StringCharacterIterator(line);
            StringBuilder argument = new StringBuilder();
            int doubleQuotesCount = 0;
            int singleQuotesCount = 0;

            for (char c = iterator.first(); c != DONE; c = iterator.next()) {
                switch (c) {
                    case '"':
                        doubleQuotesCount++;
                        break;
                    case '\'':
                        singleQuotesCount++;
                        break;
                    case ' ':
                        if (doubleQuotesCount % 2 != 0 || singleQuotesCount % 2 != 0) {
                            argument.append(c);
                            break;
                        } else {
                            char previous = iterator.previous();

                            if (previous == '\\') {
                                argument.setLength(argument.length() - 1); //delete last symbol
                                argument.append(' ');
                            } else {
                                args.add(argument.toString());
                                argument.setLength(0);
                            }

                            iterator.next();
                            break;
                        }
                    default:
                        argument.append(c);
                        break;
                }
            }

            if (argument.length() != 0) {
                args.add(argument.toString());
            }
        } else {
            args = Arrays.asList(line.split(" "));
        }

        return args;
    }

    public boolean isOsUnix() {
        return IS_OS_LINUX || IS_OS_MAC || IS_OS_MAC_OSX;
    }

    public boolean isOsWindows() {
        return IS_OS_WINDOWS;
    }
}
