package com.haulmont.addon.admintools.web.screens.jpql

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.components.autocomplete.AutoCompleteSupport
import com.haulmont.cuba.gui.components.autocomplete.JpqlSuggestionFactory
import com.haulmont.cuba.gui.components.autocomplete.Suggestion
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

import static java.util.Collections.emptyList
import static org.apache.commons.lang.StringUtils.containsIgnoreCase
import static org.apache.commons.lang.StringUtils.substringBefore

@Component('admintools_JpqlSuggesterHelper')
class JpqlSuggesterHelper {

    @Inject
    protected Metadata metadata

    List<Suggestion> getHint(SourceCodeEditor sender, String text, int senderCursorPosition) {
        def wordBeforeCursor = getLastWord(text.substring(0, senderCursorPosition))

        if (wordBeforeCursor.contains('.')) {
            return tryGetAttributeNames(wordBeforeCursor, sender, text, senderCursorPosition)
        }

        return getEntitiesNames(wordBeforeCursor, sender, senderCursorPosition)
    }

    protected List<Suggestion> tryGetAttributeNames(String wordBeforeCursor, SourceCodeEditor sender, String text, int senderCursorPosition) {
        Map<String, MetaClass> aliases = getAliases(text)
        def alias = substringBefore(wordBeforeCursor, '.')

        if (aliases.containsKey(alias)) {
            String metaClassName = aliases.get(alias).getName()
            def query = "select ${alias} from ${metaClassName} ${alias} where ${wordBeforeCursor}"

            return JpqlSuggestionFactory.requestHint(query, query.length() - 1, sender.getAutoCompleteSupport(), senderCursorPosition)
        }

        return emptyList()
    }

    protected List<Suggestion> getEntitiesNames(String wordBeforeCursor, SourceCodeEditor sender, int senderCursorPosition) {
        Collection<MetaClass> persistentClasses = metadata.getTools().getAllPersistentMetaClasses()
        Collection<MetaClass> embeddableClasses = metadata.getTools().getAllEmbeddableMetaClasses()

        int position = senderCursorPosition - wordBeforeCursor.length()

        return Stream.concat(persistentClasses.stream(), embeddableClasses.stream())
                .filter({ metaClass ->
                    wordBeforeCursor.isAllWhitespace() ||
                    containsIgnoreCase(metaClass.name, wordBeforeCursor)
                })
                .map({ metaClass ->
                    String className = metaClass.name
                    new Suggestion(sender as AutoCompleteSupport, className, className, className, position, position)
                })
                .collect(Collectors.toList() as Collector<? super Object, Object, Object>) as List<Suggestion>
    }

    protected Map<String, MetaClass> getAliases(String text) {
        Pattern pattern = Pattern.compile('[a-zA-Z0-9]+\\$[a-zA-Z0-9]+\\s+[a-zA-Z0-9]+')
        Matcher matcher = pattern.matcher(text)

        List<String> classAliases = new ArrayList<>()

        while (matcher.find()) {
            classAliases.add(matcher.group(0))
        }

        Map<String, MetaClass> aliases = new HashMap<>()
        classAliases.forEach({ line ->
            String[] splited = line.split('\\s')
            String className = splited[0]
            String alias = splited[1]

            MetaClass metaClass = metadata.getClass(className)
            if (metaClass != null) {
                aliases.putIfAbsent(alias, metaClass)
            }
        })

        aliases
    }

    protected static String getLastWord(String text) {
        if(text.endsWith(' ')){
            return ' '
        }

        def split = text.split('\\s')

        if (split.size() > 0) {
            return split[split.size() - 1]
        }
        return ''
    }

}
