package com.haulmont.addon.admintools.gui.script_generator;

import com.haulmont.addon.admintools.global.script_generator.EntityViewSqlGenerationService;
import com.haulmont.addon.admintools.global.script_generator.ScriptGenerationOptions;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.BackgroundTaskWrapper;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;

import javax.inject.Inject;
import java.util.*;

import static com.haulmont.addon.admintools.global.script_generator.ScriptGenerationOptions.INSERT;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.ERROR;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ScriptGeneratorResult extends AbstractWindow {

    @Inject
    protected SourceCodeEditor resultScript;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected SourceCodeEditor query;
    @Inject
    protected Metadata metadata;
    @Inject
    protected LookupField entitiesMetaClasses;
    @Inject
    protected LookupField entityViews;
    @Inject
    protected GroupBoxLayout querySettings;
    @Inject
    protected LookupField generateOptions;
    @Inject
    protected EntityViewSqlGenerationService sqlGenerationService;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected TextField entityLimitField;
    @Inject
    protected ProgressBar executeProgressBar;

    @WindowParam(name = "generationMode")
    protected Enum generationMode;
    @WindowParam(name = "selectedEntities")
    protected Collection<Entity> selectedEntities;
    protected BackgroundTaskWrapper<Integer, Set<String>> connectionTaskWrapper;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        generateOptions.setOptionsEnum(ScriptGenerationOptions.class);
        generateOptions.setValue(INSERT);

        if (generationMode == null) {
            generationMode = GenerationMode.CUSTOM_QUERY;
        }

        if (generationMode.equals(GenerationMode.CUSTOM_QUERY)) {
            initEntityTypeField();
            querySettings.setVisible(true);
        }

        BackgroundTask<Integer, Set<String>> connectionTask = new BackgroundTask<Integer, Set<String>>(60, getFrame()) {
            @Override
            public Set<String> run(TaskLifeCycle<Integer> taskLifeCycle) {
                return getSQLScript();
            }

            @Override
            public void canceled() {
                executeProgressBar.setIndeterminate(false);
            }

            @Override
            public boolean handleException(Exception ex) {
                executeProgressBar.setIndeterminate(false);
                showNotification(ex.getMessage(), ERROR);
                return true;
            }

            @Override
            public boolean handleTimeoutException() {
                executeProgressBar.setIndeterminate(false);
                showNotification(getMessage("timeout"), WARNING);
                return true;
            }

            @Override
            public void done(Set<String> result) {
                if (validateAll()) {
                    printResult(result);
                }
                executeProgressBar.setIndeterminate(false);
            }
        };
        connectionTaskWrapper = new BackgroundTaskWrapper<>(connectionTask);
    }

    @Override
    protected boolean preClose(String actionId) {
        connectionTaskWrapper.cancel();
        return super.preClose(actionId);
    }

    public void windowClose() {
        this.close("windowClose");
    }

    public void execute() {
        Integer limit = entityLimitField.getValue();

        if (limit != null && limit <= 0) {
            showNotification(getMessage("entityLimitWarning"), WARNING);
            return;
        }

        executeProgressBar.setIndeterminate(true);
        connectionTaskWrapper.restart();
    }

    public void cancel() {
        connectionTaskWrapper.cancel();
    }


    public void downloadResult() {
        String script = resultScript.getRawValue();

        if (isNotBlank(script)) {
            byte[] bytes = script.getBytes(UTF_8);
            exportDisplay.show(new ByteArrayDataProvider(bytes), "result.sql");
        }
    }

    public void clear() {
        resultScript.setValue("");
    }

    protected Set<String> getSQLScript() {
        Set<String> result = new HashSet<>();

        List<Entity> entitiesForDownload;
        if (generationMode.equals(GenerationMode.CUSTOM_QUERY)) {
            entitiesForDownload = getQueryResult(query.getValue());
        } else {
            entitiesForDownload = new ArrayList<>(this.selectedEntities);
        }

        if (entitiesForDownload != null) {
            for (Entity entity : entitiesForDownload) {
                result.addAll(sqlGenerationService.generateScript(entity, entityViews.getValue(), generateOptions.getValue()));
            }
        }
        return result;
    }

    protected void printResult(Set<String> result) {
        if (result == null || result.isEmpty()) {
            showNotification(getMessage("message.noDataFound"), HUMANIZED);
            return;
        }
        resultScript.setValue("");

        StringBuilder sb = new StringBuilder();
        for (String s : result) {
            sb.append(s).append("\n");
        }
        resultScript.setValue(sb.toString());
    }

    protected List<Entity> getQueryResult(String query) {
        MetaClass metaClass = entitiesMetaClasses.getValue();
        if (metaClass == null) {
            return null;
        }

        String view = entityViews.getValue();

        LoadContext<Entity> loadContext = new LoadContext<>(metaClass);
        loadContext.setView(view);

        Integer limit = entityLimitField.getValue();

        if (limit == null) {
            loadContext.setQueryString(query);
        } else {
            loadContext.setQueryString(query).setMaxResults(limit);
        }

        return dataManager.loadList(loadContext);
    }

    protected void initEntityTypeField() {
        Map<String, MetaClass> metaClasses = new LinkedHashMap<>();

        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            metaClasses.put(metaClass.getName(), metaClass);
        }
        entitiesMetaClasses.setOptionsMap(metaClasses);

        entitiesMetaClasses.addValueChangeListener(e -> {
            setEntityViewsLookup();
            String entitiesMetaClass = ((MetaClass) entitiesMetaClasses.getValue()).getName();
            if (entitiesMetaClass != null) {
                setSimpleQuery(entitiesMetaClass);
            }
        });
    }

    protected void setSimpleQuery(String entityMetaClass) {
        query.setValue(String.format("select e from %s e", entityMetaClass));
    }

    protected void setEntityViewsLookup() {
        List<String> views = new LinkedList<>();
        views.add(View.MINIMAL);
        views.add(View.LOCAL);
        views.addAll(metadata.getViewRepository().getViewNames((MetaClass) entitiesMetaClasses.getValue()));

        entityViews.setOptionsList(views);
        entityViews.setValue(View.LOCAL);
    }
}