package com.haulmont.addon.admintools.web.screens.jpql

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.components.autocomplete.AutoCompleteSupport
import com.haulmont.cuba.gui.components.autocomplete.Suggestion
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.haulmont.cuba.web.gui.components.WebSourceCodeEditor
import de.diedavids.cuba.runtimediagnose.db.DbDiagnoseService
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import de.diedavids.cuba.runtimediagnose.web.screens.console.ConsoleFrame
import de.diedavids.cuba.runtimediagnose.web.screens.jpql.JpqlConsole

import javax.inject.Inject
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

class JpqlConsoleExtended extends JpqlConsole {

    @Inject
    DbDiagnoseService dbDiagnoseService
    @Inject
    ConsoleFrame consoleFrame
    @Inject
    ComponentsFactory componentsFactory
    @Inject
    Metadata metadata

    @Override
    void init(Map<String, Object> params) {
        params.put('diagnoseType', DiagnoseType.JPQL)
        def frame = openFrame(consoleFrame, 'console-frame', params)

        WebSourceCodeEditor console = frame.getComponent("console") as WebSourceCodeEditor
        console.setSuggester({ source, text, cursorPosition ->
            getHint(console, text, cursorPosition)
        })
    }

    protected List<Suggestion> getHint(SourceCodeEditor sender, String text, int senderCursorPosition) {
        Collection<MetaClass> persistentClasses = metadata.getTools().getAllPersistentMetaClasses()
        Collection<MetaClass> embeddableClasses = metadata.getTools().getAllEmbeddableMetaClasses()

        int startPosition = senderCursorPosition - 2 < 0 ? 0 : senderCursorPosition - 2

        Stream.concat(persistentClasses.stream(), embeddableClasses.stream())
                .map({ metaClass ->
                    String className = metaClass.getName()
                    return new Suggestion(sender as AutoCompleteSupport, className, className, '', startPosition, senderCursorPosition)
                })
                .collect(Collectors.toList() as Collector<? super Object, Object, Object>) as List<Suggestion>

    }
}
