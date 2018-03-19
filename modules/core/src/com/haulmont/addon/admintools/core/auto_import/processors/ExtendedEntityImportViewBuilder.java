package com.haulmont.addon.admintools.core.auto_import.processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.EntityImportViewBuilder;
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour;
import com.haulmont.cuba.core.entity.Entity;

import java.util.Collection;
import java.util.Map;

/**
 * There is added a case for the ONE_TO_MANY property with the type ASSOCIATION,
 * see comments 'admin-tools', line 92
 */
@SuppressWarnings({"unchecked"})
public class ExtendedEntityImportViewBuilder extends EntityImportViewBuilder {

    @Override
    protected EntityImportView buildFromJsonObject(JsonObject jsonObject, MetaClass metaClass) {
        EntityImportView view = new EntityImportView(metaClass.getJavaClass());

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String propertyName = entry.getKey();
            MetaProperty metaProperty = metaClass.getProperty(propertyName);
            if (metaProperty != null) {
                Range propertyRange = metaProperty.getRange();
                Class<?> propertyType = metaProperty.getJavaType();
                if (propertyRange.isDatatype() || propertyRange.isEnum()) {
                    if (security.isEntityAttrUpdatePermitted(metaClass, propertyName))
                        view.addLocalProperty(propertyName);
                } else if (propertyRange.isClass()) {
                    if (Entity.class.isAssignableFrom(propertyType)) {
                        if (metadataTools.isEmbedded(metaProperty)) {
                            MetaClass propertyMetaClass = metadata.getClass(propertyType);
                            JsonElement propertyJsonObject = entry.getValue();
                            if (!propertyJsonObject.isJsonObject()) {
                                throw new RuntimeException("JsonObject was expected for property " + propertyName);
                            }
                            if (security.isEntityAttrUpdatePermitted(metaClass, propertyName)) {
                                EntityImportView propertyImportView = buildFromJsonObject(propertyJsonObject.getAsJsonObject(), propertyMetaClass);
                                view.addEmbeddedProperty(propertyName, propertyImportView);
                            }
                        } else {
                            MetaClass propertyMetaClass = metadata.getClass(propertyType);
                            if (metaProperty.getType() == MetaProperty.Type.COMPOSITION) {
                                JsonElement propertyJsonObject = entry.getValue();
                                if (security.isEntityAttrUpdatePermitted(metaClass, propertyName)) {
                                    if (propertyJsonObject.isJsonNull()) {
                                        //in case of null we must add such import behavior to update the reference with null value later
                                        if (metaProperty.getRange().getCardinality() == Range.Cardinality.MANY_TO_ONE) {
                                            view.addManyToOneProperty(propertyName, ReferenceImportBehaviour.IGNORE_MISSING);
                                        } else {
                                            view.addOneToOneProperty(propertyName, ReferenceImportBehaviour.IGNORE_MISSING);
                                        }
                                    } else {
                                        if (!propertyJsonObject.isJsonObject()) {
                                            throw new RuntimeException("JsonObject was expected for property " + propertyName);
                                        }
                                        EntityImportView propertyImportView = buildFromJsonObject(propertyJsonObject.getAsJsonObject(), propertyMetaClass);
                                        if (metaProperty.getRange().getCardinality() == Range.Cardinality.MANY_TO_ONE) {
                                            view.addManyToOneProperty(propertyName, propertyImportView);
                                        } else {
                                            view.addOneToOneProperty(propertyName, propertyImportView);
                                        }
                                    }
                                }
                            } else {
                                if (security.isEntityAttrUpdatePermitted(metaClass, propertyName))
                                    if (metaProperty.getRange().getCardinality() == Range.Cardinality.MANY_TO_ONE) {
                                        view.addManyToOneProperty(propertyName, ReferenceImportBehaviour.ERROR_ON_MISSING);
                                    } else {
                                        view.addOneToOneProperty(propertyName, ReferenceImportBehaviour.ERROR_ON_MISSING);
                                    }
                            }
                        }
                    } else if (Collection.class.isAssignableFrom(propertyType)) {
                        MetaClass propertyMetaClass = metaProperty.getRange().asClass();
                        switch (metaProperty.getRange().getCardinality()) {
                            case MANY_TO_MANY:
                                if (security.isEntityAttrUpdatePermitted(metaClass, propertyName))
                                    view.addManyToManyProperty(propertyName, ReferenceImportBehaviour.ERROR_ON_MISSING, CollectionImportPolicy.REMOVE_ABSENT_ITEMS);
                                break;
                            case ONE_TO_MANY:
                                if (metaProperty.getType() == MetaProperty.Type.COMPOSITION ||
                                        //admin-tools begin
                                        metaProperty.getType() == MetaProperty.Type.ASSOCIATION) {
                                    //admin-tools end
                                    JsonElement compositionJsonArray = entry.getValue();
                                    if (!compositionJsonArray.isJsonArray()) {
                                        throw new RuntimeException("JsonArray was expected for property " + propertyName);
                                    }
                                    EntityImportView propertyImportView = buildFromJsonArray(compositionJsonArray.getAsJsonArray(), propertyMetaClass);
                                    if (security.isEntityAttrUpdatePermitted(metaClass, propertyName))
                                        view.addOneToManyProperty(propertyName, propertyImportView, CollectionImportPolicy.REMOVE_ABSENT_ITEMS);
                                }
                                break;
                        }
                    }
                }
            }
        }

        return view;
    }
}
