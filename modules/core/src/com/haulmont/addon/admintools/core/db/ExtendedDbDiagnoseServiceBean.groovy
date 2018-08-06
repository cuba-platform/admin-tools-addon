package com.haulmont.addon.admintools.core.db

import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.Query
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.cuba.core.global.UserSessionSource
import de.diedavids.cuba.runtimediagnose.db.DbDiagnoseServiceBean
import de.diedavids.cuba.runtimediagnose.db.DbQueryParser
import de.diedavids.cuba.runtimediagnose.db.DbQueryResult
import de.diedavids.cuba.runtimediagnose.db.SqlSelectResultFactory
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecution
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionFactory
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseExecutionLogService
import de.diedavids.cuba.runtimediagnose.diagnose.DiagnoseType
import groovy.sql.Sql

import javax.inject.Inject

/**
 * {@code ExtendedDbDiagnoseServiceBean} overrides class {@link DbDiagnoseServiceBean} and deletes {@link DbQueryParser}
 * from one. It is necessary for cases, where the parser marks the key words as wrong. For example, in the jpql
 * 'select g from sec$Group g' word 'Group' is marked as key word and the parser throws
 * {@link net.sf.jsqlparser.JSQLParserException}
 */
class ExtendedDbDiagnoseServiceBean extends DbDiagnoseServiceBean {

    @Inject
    protected Persistence persistence
    @Inject
    protected SqlSelectResultFactory selectResultFactory
    @Inject
    protected TimeSource timeSource
    @Inject
    protected DiagnoseExecutionLogService diagnoseExecutionLogService
    @Inject
    protected UserSessionSource userSessionSource
    @Inject
    protected DiagnoseExecutionFactory diagnoseExecutionFactory

    /*
     * deleted analyse query
     */

    @Override
    DbQueryResult runSqlDiagnose(String queryString, DiagnoseType diagnoseType) {
        DiagnoseExecution diagnoseExecution = createAdHocDiagnose(queryString, diagnoseType)
        DbQueryResult dbQueryResult
        try {
            dbQueryResult = getQueryResult(diagnoseType, queryString)
            diagnoseExecution.handleSuccessfulExecution(dbQueryResult.entities[0].toString())
            diagnoseExecutionLogService.logDiagnoseExecution(diagnoseExecution)
        } catch (Exception e) {
            diagnoseExecution.handleErrorExecution(e)
            diagnoseExecutionLogService.logDiagnoseExecution(diagnoseExecution)
            throw e
        }

        dbQueryResult
    }

    @Override
    protected DbQueryResult executeSqlStatement(Sql sql, String queryString) {
        if (queryString != null) {
            if (queryString.toLowerCase().startsWith("select ")) {
                return super.executeSqlStatement(sql, queryString)
            } else if (queryString.toLowerCase().startsWith("update ")) {
                int result = sql.executeUpdate(queryString)
                return selectResultFactory.createFromRows(Arrays.asList(result))
            } else {
                boolean result = sql.execute(queryString)
                return selectResultFactory.createFromRows(Arrays.asList(result))
            }
        } else {
            return null;
        }
    }
/**
 * deleted argument 'Statements queryStatements'
 */
    protected DbQueryResult getQueryResult(DiagnoseType diagnoseType, String queryStatement) {
        DbQueryResult sqlSelectResult
        switch (diagnoseType) {
            case DiagnoseType.JPQL:
                sqlSelectResult = executeJpqlStatement(queryStatement)
                break
            case DiagnoseType.SQL:
                def sql = createSqlConnection(persistence.dataSource)
                sqlSelectResult = executeSqlStatement(sql, queryStatement)
                break
            default:
                throw new IllegalArgumentException('DiagnoseType is not supported (' + diagnoseType + ')')
        }
        sqlSelectResult
    }

    /**
     * deleted argument 'Statements queryStatements'
     */
    protected DbQueryResult executeJpqlStatement(String queryStatement) {
        persistence.callInTransaction {
            Query q = persistence.entityManager.createQuery(queryStatement)

            if (containsDataManipulation(queryStatement)) {
                q.executeUpdate()
                new DbQueryResult()
            } else {
                selectResultFactory.createFromRows(q.resultList)
            }
        }
    }
    /**
     * added
     */
    protected static boolean containsDataManipulation(String queryStatement) {
        String[] manipulationOperations = ['insert', 'update', 'delete']
        return Arrays.stream(manipulationOperations).anyMatch({ op -> queryStatement.contains(op) })
    }

    /**
     * private modifier in the parent class
     */
    protected DiagnoseExecution createAdHocDiagnose(String sqlStatement, DiagnoseType diagnoseType) {
        def diagnoseExecution = diagnoseExecutionFactory.createAdHocDiagnoseExecution(sqlStatement, diagnoseType)
        setDiagnoseExecutionMetadata(diagnoseExecution)
        diagnoseExecution
    }

    /**
     * private modifier in the parent class
     */
    protected void setDiagnoseExecutionMetadata(DiagnoseExecution diagnoseExecution) {
        diagnoseExecution.executionTimestamp = timeSource.currentTimestamp()
        diagnoseExecution.executionUser = userSessionSource.userSession.currentOrSubstitutedUser.login
    }
}
