package com.haulmont.addon.admintools.core.script_generator;

import com.haulmont.addon.admintools.global.script_generator.EntityViewSqlGenerationService;
import com.haulmont.addon.admintools.global.script_generator.ScriptGenerationOptions;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.app.EntitySqlGenerationService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewProperty;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.JoinTable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.haulmont.chile.core.model.MetaProperty.Type.ASSOCIATION;
import static com.haulmont.chile.core.model.MetaProperty.Type.COMPOSITION;
import static com.haulmont.chile.core.model.Range.Cardinality.*;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

@Service(EntityViewSqlGenerationService.NAME)
public class EntityViewSqlGenerationServiceBean implements EntityViewSqlGenerationService {

    @Inject
    protected EntitySqlGenerationService sqlGenerationService;

    @Inject
    protected Metadata metadata;

    protected String insertTemplate = "insert into %s \n(%s, %s) \nvalues ('%s', '%s');\n";
    protected String updateTemplate = "update %s \nset %s, %s \nwhere %s='%s';\n";

    protected Set<String> scripts = new HashSet<>();

    @Override
    public Set<String> generateScript(Entity entity, String viewName, ScriptGenerationOptions scriptType) {
        scripts.clear();
        View rootView = metadata.getViewRepository().getView(entity.getMetaClass(), viewName);
        generateScript(entity, rootView, scriptType);

        return scripts;
    }

    protected void generateScript(Entity entity, View view, ScriptGenerationOptions scriptType) {
        MetaClass metaClass = entity.getMetaClass();

        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if (isViewNotContainsProperty(view, metaProperty)) {
                continue;
            }

            if (isAssociationProperty(metaProperty)) {
                ViewProperty viewProperty = view.getProperty(metaProperty.getName());

                if (isEmbedded(metaProperty) || viewProperty == null || viewProperty.getView() == null) {
                    continue;
                }

                View refView = viewProperty.getView();

                if (isReferenceProperty(metaProperty)) {
                    processReferenceProperty(entity, metaProperty, refView, scriptType);
                } else if (isCollectionProperty(metaProperty)) {
                    processCollectionProperty(entity, metaProperty, refView, scriptType);
                }
            }
        }

        scripts.add(generateScript(entity, scriptType));
    }


    protected void generateBundleTableScript(Entity entity, Collection<Entity> refEntities, MetaProperty metaProperty, ScriptGenerationOptions scriptType) {
        JoinTable annotation = metaProperty.getAnnotatedElement().getAnnotation(JoinTable.class);
        String tableName = annotation.name();
        String joinColumnName = annotation.joinColumns()[0].name();
        String inverseJoinColumnName = annotation.inverseJoinColumns()[0].name();

        switch (scriptType) {
            case INSERT:
                for (Entity refEntity : refEntities) {
                    scripts.add(format(insertTemplate, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId()));
                }
                break;
            case UPDATE:
                for (Entity refEntity : refEntities) {
                    scripts.add(format(updateTemplate, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId()));
                }
                break;
            case INSERT_UPDATE:
                for (Entity refEntity : refEntities) {
                    scripts.add(format(insertTemplate, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId()));
                    scripts.add(format(updateTemplate, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId()));
                }
            default:
                break;
        }
    }

    protected String generateScript(Entity entity, ScriptGenerationOptions scriptType) {
        switch (scriptType) {
            case INSERT:
                return sqlGenerationService.generateInsertScript(entity);
            case UPDATE:
                return sqlGenerationService.generateUpdateScript(entity);
            case INSERT_UPDATE:
                return sqlGenerationService.generateInsertScript(entity) +
                        "\n" +
                        sqlGenerationService.generateUpdateScript(entity);
            default:
                return "";
        }
    }

    protected boolean isViewNotContainsProperty(View view, MetaProperty metaProperty) {
        return !view.containsProperty(metaProperty.getName());
    }

    protected boolean isAssociationProperty(MetaProperty metaProperty) {
        MetaProperty.Type propertyType = metaProperty.getType();
        return propertyType.equals(ASSOCIATION) || propertyType.equals(COMPOSITION);
    }

    protected boolean isReferenceProperty(MetaProperty metaProperty) {
        Range.Cardinality cardinality = metaProperty.getRange().getCardinality();
        return cardinality.equals(ONE_TO_ONE) || cardinality.equals(MANY_TO_ONE);
    }

    protected boolean isCollectionProperty(MetaProperty metaProperty) {
        Range.Cardinality cardinality = metaProperty.getRange().getCardinality();
        return cardinality.equals(ONE_TO_MANY) || cardinality.equals(MANY_TO_MANY);
    }

    protected boolean isManyToManyProperty(MetaProperty metaProperty) {
        Range.Cardinality cardinality = metaProperty.getRange().getCardinality();
        return cardinality.equals(MANY_TO_MANY);
    }

    protected boolean isEmbedded(MetaProperty metaProperty) {
        Map<String, Object> annotations = metaProperty.getAnnotations();

        if (isNotEmpty(annotations)) {
            Object embedded = annotations.get("cuba.embedded");
            return embedded != null && ((Boolean) embedded);
        }

        return false;
    }

    protected void processReferenceProperty(Entity entity, MetaProperty metaProperty, View refView, ScriptGenerationOptions scriptType) {
        Entity refEntity = entity.getValue(metaProperty.getName());

        if (refEntity != null) {
            generateScript(refEntity, refView, scriptType);
        }
    }

    protected void processCollectionProperty(Entity entity, MetaProperty metaProperty, View refView, ScriptGenerationOptions scriptType) {
        Collection<Entity> refEntities = entity.getValue(metaProperty.getName());

        if (isNotEmpty(refEntities)) {
            for (Entity refEntity : refEntities) {
                generateScript(refEntity, refView, scriptType);
            }

            if (isManyToManyProperty(metaProperty)) {
                generateBundleTableScript(entity, refEntities, metaProperty, scriptType);
            }
        }
    }


}
