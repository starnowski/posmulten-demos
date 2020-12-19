package com.github.starnowski.posmulten.demos;

import com.github.starnowski.posmulten.demos.hibernate.TenantContextAwareInvoker;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.rls.function.ISetCurrentTenantIdFunctionInvocationFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static com.github.starnowski.posmulten.demos.TestUtils.*;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

public class NativeSqlWithRlsTest extends AbstractWebEnvironmentSpringBootTestWithTestProfile {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("ownerJdbcTemplate")
    protected JdbcTemplate ownerJdbcTemplate;

    @Autowired
    private TenantContextAwareInvoker tenantContextAwareInvoker;

    @Autowired
    protected ISharedSchemaContext iSharedSchemaContext;

    protected ISetCurrentTenantIdFunctionInvocationFactory setCurrentTenantIdFunctionInvocationFactory;

    @Before
    public void setUp() {
        setCurrentTenantIdFunctionInvocationFactory = iSharedSchemaContext.getISetCurrentTenantIdFunctionInvocationFactory();
    }

    @Test
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldReadRecordFromSameTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        int result = returnIntForStatement(jdbcTemplate, "SELECT COUNT(*) FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'", statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds"));

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldReadRecordFromSameTenantWhenTenantIsStoredInThreadContext() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        int result = tenantContextAwareInvoker.tryExecuteTransactionInCorrectTenantContext(() ->
                returnIntForStatement(jdbcTemplate, "SELECT COUNT(*) FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'"), "xds");

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldNotAbleToReadRecordFromOtherTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        int result = returnIntForStatement(jdbcTemplate, "SELECT COUNT(*) FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'", statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds1"));

        // then
        assertThat(result).isZero();
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldNotAbleToReadRecordFromOtherTenantWhenTenantIsStoredInThreadContext() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        int result = tenantContextAwareInvoker.tryExecuteTransactionInCorrectTenantContext(() ->
                returnIntForStatement(jdbcTemplate, "SELECT COUNT(*) FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'"), "xds1");

        // then
        assertThat(result).isZero();
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldUpdateRecordForSameTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski'")).isEqualTo(1);

        // when
        jdbcTemplate.execute(statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds") + "UPDATE user_info SET username = 'starnowski1' WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");

        // then
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski'")).isZero();
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski1'")).isEqualTo(1);
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldNotAbleToUpdateRecordFromOtherTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski'")).isEqualTo(1);

        // when
        jdbcTemplate.execute(statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds1") + "UPDATE user_info SET username = 'starnowski1' WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");

        // then
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski'")).isEqualTo(1);
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds' AND username = 'starnowski1'")).isZero();
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldDeleteRecordForSameTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        jdbcTemplate.execute(statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds") + "DELETE FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");

        // then
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isZero();
    }

    @Test
    @Sql(value = {GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, CLEAR_DATABASE_SCRIPT_PATH, TEST_BASIC_DATA_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    public void shouldNotAbleToDeleteRecordFromOtherTenant() {
        // given
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);

        // when
        jdbcTemplate.execute(statementSettingCurrentTenantVariable(setCurrentTenantIdFunctionInvocationFactory, "xds1") + "DELETE FROM user_info WHERE user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'");

        // then
        assertThat(TestUtils.countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' AND tenant_id = 'xds'")).isEqualTo(1);
    }
}
