package com.haulmont.addon.admintools.script_generator;

import com.haulmont.addon.admintools.app.EntityViewSqlGenerationService;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.SourceCodeEditor;

import javax.inject.Inject;
import java.util.*;

public class GenerateScriptsResult extends AbstractWindow {

    @Inject
    private SourceCodeEditor resultScript;

    @Inject
    private DataManager dataManager;

    @Inject
    private SourceCodeEditor query;

    @WindowParam(name = "generationMode")
    private Enum generationMode;

    @Inject
    private Metadata metadata;

    @Inject
    private LookupField entitiesMetaClasses;

    @Inject
    private LookupField entityViews;

    @Inject
    private GroupBoxLayout querySettings;

    @WindowParam(name = "selectedEntities")
    private Collection<Entity> selectedEntities;

    @Inject
    private LookupField generateOptions;

    @Inject
    private EntityViewSqlGenerationService sqlGenerationService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        generateOptions.setOptionsEnum(ScriptGenerationOptions.class);
        generateOptions.setValue(ScriptGenerationOptions.INSERT);

        if (generationMode == null) {
            generationMode = GenerationMode.CUSTOM_QUERY;
        }

        if (generationMode.equals(GenerationMode.CUSTOM_QUERY)) {
            initEntityTypeField();
            querySettings.setVisible(true);
        }
    }

    public void windowClose() {
        this.close("windowClose");
    }

    public void execute() {
        if (validateAll()) {
            Set<String> result = getSQLScript();

            if (result == null || result.isEmpty()) {
                showNotification(getMessage("message.noDataFound"), NotificationType.HUMANIZED);
                return;
            }

            resultScript.setValue("");

            StringBuilder sb = new StringBuilder();
            result.forEach(s ->
                    sb.append(s).append("\n")
            );

            resultScript.setValue(sb.toString());
        }
    }

    private Set<String> getSQLScript() {
        Set<String> result = new HashSet<>();

        Collection<Entity> entitiesForDownload;
        if (generationMode.equals(GenerationMode.CUSTOM_QUERY)) {
            entitiesForDownload = getQueryResult(query.getValue());
        } else {
            entitiesForDownload = this.selectedEntities;
        }

        if (entitiesForDownload != null) {
            Enum<ScriptGenerationOptions> generateOption = generateOptions.getValue();

            if (ScriptGenerationOptions.INSERT.equals(generateOption)) {
                entitiesForDownload.forEach(entity ->
                        result.addAll(sqlGenerationService.generateInsertScript(entity, entityViews.getValue()))
                );
            } else if (ScriptGenerationOptions.UPDATE.equals(generateOption)) {
                entitiesForDownload.forEach(entity ->
                        result.addAll(sqlGenerationService.generateUpdateScript(entity, entityViews.getValue()))
                );
            } else if(ScriptGenerationOptions.INSERT_UPDATE.equals(generateOption)) {
                entitiesForDownload.forEach(entity -> {
                    result.addAll(sqlGenerationService.generateInsertScript(entity, entityViews.getValue()));
                    result.addAll(sqlGenerationService.generateUpdateScript(entity, entityViews.getValue()));
                });
            } else if(ScriptGenerationOptions.SELECT.equals(generateOption)){
                entitiesForDownload.forEach(entity ->
                        result.addAll(sqlGenerationService.generateSelectScript(entity, entityViews.getValue()))
                );
            }
        }

        return result;
    }

    private Collection<Entity> getQueryResult(String query) {
        MetaClass metaClass = entitiesMetaClasses.getValue();
        if (metaClass == null) {
            return null;
        }

        String view = entityViews.getValue();

        LoadContext loadContext = new LoadContext<>(metaClass);
        loadContext.setView(view);
        loadContext.setQueryString(query);

        return dataManager.loadList(loadContext);
    }

    private void initEntityTypeField() {
        Map metaClasses = new LinkedHashMap<>();

        metadata.getTools().getAllPersistentMetaClasses().forEach(metaClass ->
                metaClasses.put(metaClass.getName(), metaClass)
        );
        entitiesMetaClasses.setOptionsMap(metaClasses);

        entitiesMetaClasses.addValueChangeListener(e -> {
            setEntityViewsLookup();
            String entitiesMetaClass = ((MetaClass) entitiesMetaClasses.getValue()).getName();
            if (entitiesMetaClass != null) {
                setSimpleQuery(entitiesMetaClass);
            }
        });
    }

    private void setSimpleQuery(String entityMetaClass) {
        query.setValue(String.format("select e from %s e", entityMetaClass));
    }

    private void setEntityViewsLookup() {
        List<String> views = new LinkedList<>();
        views.add(View.MINIMAL);
        views.add(View.LOCAL);
        views.addAll(metadata.getViewRepository().getViewNames((MetaClass) entitiesMetaClasses.getValue()));

        entityViews.setOptionsList(views);
        entityViews.setValue(View.LOCAL);
    }
}