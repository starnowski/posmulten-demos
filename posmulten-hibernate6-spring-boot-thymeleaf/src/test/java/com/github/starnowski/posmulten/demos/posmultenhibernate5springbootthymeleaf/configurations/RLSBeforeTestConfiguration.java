package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.hibernate.hibernate6.context.SharedSchemaContextProvider;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.db.DatabaseOperationExecutor;
import com.github.starnowski.posmulten.postgresql.core.db.operations.exceptions.ValidationDatabaseOperationsException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.sql.DataSource;

import java.sql.SQLException;

import static com.github.starnowski.posmulten.postgresql.core.db.DatabaseOperationType.*;

@Configuration
public class RLSBeforeTestConfiguration implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    @Qualifier("ownerDataSource")
    private DataSource ownerDataSource;
    @Autowired
    @Qualifier("schema_session_factory")
    private SessionFactory sessionFactory;
    private final DatabaseOperationExecutor databaseOperationExecutor = new DatabaseOperationExecutor();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        SharedSchemaContextProvider sharedSchemaContextProvider = sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(SharedSchemaContextProvider.class);
        ISharedSchemaContext sharedSchemaContext = sharedSchemaContextProvider.getSharedSchemaContext();
        try {
            this.databaseOperationExecutor.execute(ownerDataSource, sharedSchemaContext.getSqlDefinitions(), LOG_ALL);
            this.databaseOperationExecutor.execute(ownerDataSource, sharedSchemaContext.getSqlDefinitions(), CREATE);
            this.databaseOperationExecutor.execute(ownerDataSource, sharedSchemaContext.getSqlDefinitions(), VALIDATE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ValidationDatabaseOperationsException e) {
            throw new RuntimeException(e);
        }
    }
}
