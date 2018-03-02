package com.haulmont.addon.admintools.console;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.text.CharacterIterator.DONE;

@Component("admintools_ConsoleTool")
public class ConsoleTool {

    @Inject
    protected ConsolePrecondition cprecond;

    public List<String> parseArgs(String line) {
        List<String> args = new ArrayList<>();

        if (cprecond.isOsUnix()) {
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
                        if (doubleQuotesCount % 2 == 1 || singleQuotesCount % 2 == 1) {
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
}
