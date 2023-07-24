package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.utils;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.PrimaryDataSourceConfiguration.SET_CURRENT_TENANT_FUNCTION_NAME;

public class TestUtils {

    public static final String CLEAR_DATABASE_SCRIPT_PATH = "/com/github/starnowski/posmulten/demos/posmultenhibernate5springbootthymeleaf/clean-database.sql";
    public static final String MULTI_TENANT_CONTEXT_AWARE_CONTROLLER_TEST_SCRIPT_PATH = "/com/github/starnowski/posmulten/demos/posmultenhibernate5springbootthymeleaf/controllers/MultiTenantContextAwareControllerTest-script.sql";

    public static int countNumberOfRecordsWhere(JdbcTemplate jdbcTemplate, String table, String condition) {
        return countNumberOfRecordsWhereByTenantId(jdbcTemplate, table, condition, TenantUser.ROOT_TENANT_ID);
    }

    public static int countNumberOfRecordsWhereByTenantId(JdbcTemplate jdbcTemplate, String table, String condition, String tenantId) {
        return jdbcTemplate.execute(new StatementCallback<Integer>() {
            @Override
            public Integer doInStatement(Statement statement) throws SQLException, DataAccessException {
                statement.execute(statementSettingCurrentTenantVariable(tenantId));
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + condition);
                rs.next();
                return rs.getInt(1);
            }
        });
    }

    public static String statementSettingCurrentTenantVariable(String tenantId) {
        return "SELECT " + SET_CURRENT_TENANT_FUNCTION_NAME + "('" + tenantId + "');";
    }
}
