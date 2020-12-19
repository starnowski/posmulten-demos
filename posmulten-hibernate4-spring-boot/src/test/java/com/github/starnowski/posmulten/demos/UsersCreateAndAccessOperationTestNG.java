package com.github.starnowski.posmulten.demos;

import com.github.starnowski.posmulten.demos.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;

import static com.github.starnowski.posmulten.demos.TestUtils.*;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

public class UsersCreateAndAccessOperationTestNG extends TestNGSpringContextWithoutGenericTransactionalSupportTests{

    private static final String PASSWORD = "pass";

    @Qualifier("ownerDataSource")
    @Autowired
    private DataSource ownerDataSource;

    @Autowired
    private TestRestTemplate restTemplate;

    @DataProvider(name = "userData")
    protected static Object[][] userData()
    {
        return new Object[][]{
                {new UserDto().setUsername("johndoe").setPassword(PASSWORD), "ten1"},
                {new UserDto().setUsername("marydoe").setPassword(PASSWORD), "xds"}
        };
    }

    @BeforeMethod
    public void setUp()
    {
        this.setDataSource(ownerDataSource);
    }

    @Test
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, INSERT_TESTS_TENANTS_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void prepareDatabase()
    {

    }

    @Test(dependsOnMethods = "prepareDatabase")
    public void createUser(UserDto user, String tenant)
    {
        // given
        String url = appTenantUrl(tenant, "users");

        // when
        restTemplate.postForEntity(url, user, UserDto.class);

        // then
        //TODO
    }

    @Test(dependsOnMethods = "createUser", alwaysRun = true)
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    public void deleteData()
    {

    }
}
