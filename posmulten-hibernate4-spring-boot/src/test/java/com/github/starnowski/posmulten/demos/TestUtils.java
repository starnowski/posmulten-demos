package com.github.starnowski.posmulten.demos;

import com.github.starnowski.posmulten.postgresql.core.rls.function.ISetCurrentTenantIdFunctionInvocationFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestUtils {

    public static final String CLEAR_DATABASE_SCRIPT_PATH = "/com/github/starnowski/posmulten/demos/clean-database.sql";
    public static final String TEST_BASIC_DATA_SCRIPT_PATH = "/com/github/starnowski/posmulten/demos/test-basic-data.sql";

    public static int countNumberOfRecordsWhere(JdbcTemplate jdbcTemplate, String table, String condition)
    {
        return countNumberOfRecordsWhereByTenantId(jdbcTemplate, table, condition);
    }

    public static int countNumberOfRecordsWhereByTenantId(JdbcTemplate jdbcTemplate, String table, String condition)
    {
        return jdbcTemplate.execute(new StatementCallback<Integer>() {
            @Override
            public Integer doInStatement(Statement statement) throws SQLException, DataAccessException {
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + condition);rs.next();
                return rs.getInt(1);
            }
        });
    }

    public static int returnIntForStatement(JdbcTemplate jdbcTemplate, String selectStatement)
    {
        return returnIntForStatement(jdbcTemplate, selectStatement, null);
    }

    public static int returnIntForStatement(JdbcTemplate jdbcTemplate, String selectStatement, String setCurrentTenantIdStatement)
    {
        return jdbcTemplate.execute(new StatementCallback<Integer>() {
            @Override
            public Integer doInStatement(Statement statement) throws SQLException, DataAccessException {
                if (setCurrentTenantIdStatement != null)
                {
                    statement.execute(setCurrentTenantIdStatement);
                }
                ResultSet rs = statement.executeQuery(selectStatement);rs.next();
                return rs.getInt(1);
            }
        });
    }

    public static String statementSettingCurrentTenantVariable(ISetCurrentTenantIdFunctionInvocationFactory setCurrentTenantIdFunctionInvocationFactory, String tenantId) {
        return setCurrentTenantIdFunctionInvocationFactory.generateStatementThatSetTenant(tenantId);
    }
}
