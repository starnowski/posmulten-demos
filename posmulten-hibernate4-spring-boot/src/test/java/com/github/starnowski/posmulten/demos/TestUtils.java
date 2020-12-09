package com.github.starnowski.posmulten.demos;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestUtils {

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
}
