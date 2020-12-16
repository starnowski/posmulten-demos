package com.github.starnowski.posmulten.demos.controllers

import com.github.starnowski.posmulten.demos.SpecificationWithSpringBootWebEnvironmentTestContext
import com.github.starnowski.posmulten.demos.TestUtils
import com.github.starnowski.posmulten.demos.dto.TenantDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import spock.lang.Unroll

import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED

@SqlGroup([
@Sql(value = [ TestUtils.GRANT_ACCESS_TO_DB_USER_SCRIPT_PATH, TestUtils.CLEAR_DATABASE_SCRIPT_PATH, TestUtils.TEST_BASIC_DATA_SCRIPT_PATH],
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = "ownerDataSource", transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = BEFORE_TEST_METHOD),
@Sql(value = TestUtils.CLEAR_DATABASE_SCRIPT_PATH,
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = "ownerDataSource", transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = AFTER_TEST_METHOD)])
class TenantControllerTest extends SpecificationWithSpringBootWebEnvironmentTestContext {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    JdbcTemplate jdbcTemplate

    def slurper = new groovy.json.JsonSlurper()

    @Unroll
    def "should modify company data base on request body"()
    {
        given:
            TenantDto dto = new TenantDto()
            dto.setName(tenant)

        when:
            def result = restTemplate.postForEntity("/app/tenants", dto, TenantDto.class)

        then:
            result.name == tenant

        where:
            tenant << ["xdds", "ten1", "some_com_ten"]
    }
}
