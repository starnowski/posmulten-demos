package com.github.starnowski.posmulten.demos.controllers

import com.github.starnowski.posmulten.demos.SpecificationWithSpringBootWebEnvironmentTestContext
import com.github.starnowski.posmulten.demos.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import spock.lang.Unroll

import static com.github.starnowski.posmulten.demos.TestUtils.*
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED

@SqlGroup([
        @Sql(value = [ CLEAR_DATABASE_SCRIPT_PATH, INSERT_TESTS_TENANTS_SCRIPT_PATH],
                config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
                executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
                config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
                executionPhase = AFTER_TEST_METHOD)])
class UsersControllerTest extends SpecificationWithSpringBootWebEnvironmentTestContext {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    @Qualifier("ownerJdbcTemplate")
    protected JdbcTemplate ownerJdbcTemplate

    @Unroll
    def "should create user (#user) based on request body"()
    {
        given:
            assertThat(countUsersWithSpecifiedName(user.getUsername())).isZero()
            def url = appTenantUrl(tenant, "users")

        when:
            def result = restTemplate.postForEntity(url, user, UserDto.class)

        then:
            result.body.username == user.getUsername()

        and: "number of added users should be correct"
            countUsersWithSpecifiedName(user.getUsername()) == 1

        where:
            user                                                    |   tenant
            new UserDto().setUsername("John").setPassword("pass1")  |   "ten1"
            new UserDto().setUsername("Bill").setPassword("pass1")  |   "xds"
    }

    int countUsersWithSpecifiedName(String name)
    {
        countNumberOfRecordsWhere(ownerJdbcTemplate, "user_info", "username = '" + name + "'")
    }
}
