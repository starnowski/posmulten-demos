package com.github.starnowski.posmulten.demos.hibernate.multitenancy;

import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.rls.function.ISetCurrentTenantIdFunctionPreparedStatementInvocationFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.starnowski.posmulten.demos.util.TenantContext.INVALID_TENANT_ID;

@Slf4j
@Component
public class ConfigurableMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    private ISharedSchemaContext iSharedSchemaContext;

    @Autowired
    private DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        ISetCurrentTenantIdFunctionPreparedStatementInvocationFactory factory = iSharedSchemaContext.getISetCurrentTenantIdFunctionPreparedStatementInvocationFactory();
        try {
            log.trace("Setting tenant " + tenantIdentifier);
            PreparedStatement statement = connection.prepareStatement(factory.returnPreparedStatementThatSetCurrentTenant());
            statement.setString(1, tenantIdentifier);//TODO Try to resolve tenant column type (in case if type is different than string type)
            statement.execute();
        } catch (SQLException e) {
            throw new HibernateException(
                    "Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
                    e
            );
        }
        return connection;
    }

    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        ISetCurrentTenantIdFunctionPreparedStatementInvocationFactory factory = iSharedSchemaContext.getISetCurrentTenantIdFunctionPreparedStatementInvocationFactory();
        try {
            PreparedStatement statement = connection.prepareStatement(factory.returnPreparedStatementThatSetCurrentTenant());
            statement.setString(1, INVALID_TENANT_ID);//TODO Try to resolve tenant column type (in case if type is different than string type)
            statement.execute();
        } catch (SQLException e) {
            throw new HibernateException(
                    "Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
                    e
            );
        }
        connection.close();
    }


    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}