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

package de.diedavids.cuba.runtimediagnose.db

import groovy.sql.Sql
import net.sf.jsqlparser.statement.Statement
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class DbSqlExecutor {

    @Inject
    DbQueryParser dbQueryParser

    @Inject
    SqlSelectResultFactory selectResultFactory


    DbQueryResult executeStatement(Sql sql, Statement sqlStatement) {

        def rows = []

        def queryString = sqlStatement.toString()

        if (dbQueryParser.isSelect(sqlStatement)) {
            rows = sql.rows(queryString)
        }
        else if (dbQueryParser.isDataManipulation(sqlStatement)) {
            sql.executeUpdate(queryString)
        }
        else {
            rows = sql.execute(queryString)
        }
        selectResultFactory.createFromRows(rows)
    }

}