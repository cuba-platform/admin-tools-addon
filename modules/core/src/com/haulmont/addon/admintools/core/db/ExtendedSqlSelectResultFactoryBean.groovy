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

package com.haulmont.addon.admintools.core.db

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.DatatypeFormatter
import de.diedavids.cuba.runtimediagnose.db.DbQueryResult
import de.diedavids.cuba.runtimediagnose.db.SqlSelectResultFactoryBean

import javax.inject.Inject
import java.sql.Timestamp
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * {@code ExtendedSqlSelectResultFactoryBean} overrides class {@link SqlSelectResultFactoryBean}. There are added
 * cases if in the method {@link #createFromRows} rows have type Array or simple object. It can be if jpql was
 * with aliases like, 'select u.id, u.login from sec$User u' or 'select u.id from sec$User u' respectively.
 * See comments 'Admin-tools added' and 'Admin-tools end'
 */
class ExtendedSqlSelectResultFactoryBean extends SqlSelectResultFactoryBean {

    @Inject
    DatatypeFormatter datatypeFormatter

    protected final String COLUMN = 'Column'

    @Override
    DbQueryResult createFromRows(List<Object> rows) {
        def result = new DbQueryResult()
        def queryValue = rows[0]

        if (queryValue instanceof Entity) {
            MetaClass queryValueMetaClass = queryValue.metaClass
            for (def prop : queryValueMetaClass.properties) {
                if (!Collection.isAssignableFrom(prop.javaType)) {
                    result.addColumn(prop.name)
                }
            }

            rows.each { result.addEntity(createKeyValueEntity(it.properties)) }
        } else if (queryValue instanceof Map) {
            ((Map) queryValue).keySet().each { result.addColumn(it.toString()) }
            rows.each { result.addEntity(createKeyValueEntity((Map) it)) }

            //Admin-tools added
        } else if (queryValue.getClass().isArray()) {
            int columnsCount = (queryValue as Object[]).size()
            List<String> columns = IntStream.range(0, columnsCount).mapToObj({ i -> "${COLUMN} ${i}" }).collect(Collectors.toList())
            columns.forEach { result.addColumn(it) }

            rows.each {
                def fieldValues = it as Object[]
                Map<String, Object> content = new HashMap<>()

                for (int i = 0; i < columns.size(); i++) {
                    content.put(columns.get(i), fieldValues[i])
                }
                result.addEntity(createKeyValueEntity(content))
            }
        } else { //if one column
            result.addColumn(COLUMN)
            rows.each { result.addEntity(createKeyValueEntity(Collections.singletonMap(COLUMN, it))) }
        }
        //Admin-tools end

        result
    }

    protected KeyValueEntity createKeyValueEntity(Map<String, Object> content) {
        def kv = new KeyValueEntity()
        content.each { k, v ->
            def displayedValue = v.toString()
            if (v instanceof Timestamp) {
                displayedValue = datatypeFormatter.formatDateTime(new Date(v.time))
            } else if (v instanceof Entity) {
                displayedValue = v.id.toString()
            }
            kv.setValue(k, displayedValue)
        }
        kv
    }

}
