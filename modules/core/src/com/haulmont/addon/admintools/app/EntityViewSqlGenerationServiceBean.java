package com.haulmont.addon.admintools.app;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.app.EntitySqlGenerationService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.JoinTable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.haulmont.addon.admintools.app.EntityViewSqlGenerationServiceBean.ScriptType.INSERT;
import static com.haulmont.addon.admintools.app.EntityViewSqlGenerationServiceBean.ScriptType.SELECT;
import static com.haulmont.addon.admintools.app.EntityViewSqlGenerationServiceBean.ScriptType.UPDATE;
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

    protected enum ScriptType {INSERT, UPDATE, SELECT}

    protected String INSERT_TEMPLATE = "insert into %s \n(%s, %s) \nvalues ('%s', '%s');\n";
    protected String UPDATE_TEMPLATE = "update %s \nset %s, %s \nwhere %s='%s';\n";
    protected String SELECT_TEMPLATE = "select e.$s e.$s from %s e";

    protected Set<String> scripts = new HashSet<>();

    @Override
    public Set<String> generateInsertScript(Entity entity, String viewName) {
        return generateScript(entity, viewName, INSERT);
    }

    @Override
    public Set<String> generateUpdateScript(Entity entity, String viewName) {
        return generateScript(entity, viewName, UPDATE);
    }

    @Override
    public Set<String> generateSelectScript(Entity entity, String viewName) {
        return generateScript(entity, viewName, SELECT);
    }

    protected Set<String> generateScript(Entity entity, String viewName, ScriptType scriptType) {
        scripts.clear();
        View rootView = metadata.getViewRepository().getView(entity.getMetaClass(), viewName);
        generateScript(entity, rootView, scriptType);

        return scripts;
    }

    protected void generateScript(Entity entity, View view, ScriptType scriptType) {
        MetaClass metaClass = entity.getMetaClass();

        metaClass.getProperties().forEach(metaProperty -> {
            if (isViewNotContainsProperty(view, metaProperty)) {
                return;
            }

            if (isAssociationProperty(metaProperty)) {

                if (isEmbedded(metaProperty)) {
                    return;
                }

                if (isReferenceProperty(metaProperty)) {
                    Entity refEntity = entity.getValue(metaProperty.getName());
                    View refView = view.getProperty(metaProperty.getName()).getView();

                    if (refEntity != null) {
                        generateScript(refEntity, refView, scriptType);
                    }
                } else if (isCollectionProperty(metaProperty)) {
                    Collection<Entity> refEntities = entity.getValue(metaProperty.getName());
                    View refView = view.getProperty(metaProperty.getName()).getView();

                    if (isNotEmpty(refEntities)) {
                        refEntities.forEach(refEntity -> generateScript(refEntity, refView, scriptType));

                        if (isManyToManyProperty(metaProperty)) {
                            generateBundleTableScript(entity, refEntities, metaProperty, scriptType);
                        }
                    }
                }
            }

        });

        scripts.add(generateScript(entity, scriptType));
    }

    protected void generateBundleTableScript(Entity entity, Collection<Entity> refEntities, MetaProperty metaProperty, ScriptType scriptType) {
        JoinTable annotation = metaProperty.getAnnotatedElement().getAnnotation(JoinTable.class);
        String tableName = annotation.name();
        String joinColumnName = annotation.joinColumns()[0].name();
        String inverseJoinColumnName = annotation.inverseJoinColumns()[0].name();

        switch (scriptType) {
            case SELECT:
                scripts.add(format(SELECT_TEMPLATE, joinColumnName, inverseJoinColumnName, tableName));
                break;
            case INSERT:
                refEntities.forEach(refEntity -> scripts.add(
                        format(INSERT_TEMPLATE, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId())
                ));
                break;
            case UPDATE:
                refEntities.forEach(refEntity -> scripts.add(
                        format(UPDATE_TEMPLATE, tableName, joinColumnName, inverseJoinColumnName, entity.getId(), refEntity.getId())
                ));
                break;
            default:
                break;
        }
    }

    protected String generateScript(Entity entity, ScriptType scriptType) {
        switch (scriptType) {
            case INSERT:
                return sqlGenerationService.generateInsertScript(entity);
            case UPDATE:
                return sqlGenerationService.generateUpdateScript(entity);
            case SELECT:
                return sqlGenerationService.generateSelectScript(entity);
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


}
