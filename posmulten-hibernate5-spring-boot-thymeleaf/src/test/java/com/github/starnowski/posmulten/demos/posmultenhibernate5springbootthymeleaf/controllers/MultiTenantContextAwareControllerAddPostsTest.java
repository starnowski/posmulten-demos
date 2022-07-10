package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultiTenantContextAwareControllerAddPostsTest extends AbstractControllerTest{

    @Autowired
    WebClient webClient;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private HtmlPage currentPage;

    @Order(1)
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, MULTI_TENANT_CONTEXT_AWARE_CONTROLLER_TEST_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    @org.junit.jupiter.api.Test
    public void setUp() {
        webClient.getCookieManager().clearCookies();
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @Order(2)
    @org.junit.jupiter.api.Test
    public void shouldRedirectToLoginPageForExistingDomain() throws Exception {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'polish.dude.eu'")).isEqualTo(1);

        // when
        currentPage = this.webClient.getPage("/app/polish.dude.eu/posts");
        WebResponse response = currentPage.getWebResponse();
        int responseStatus = response.getStatusCode();

        // then
        assertThat(responseStatus).isEqualTo(OK.value());
        assertThat(response.getWebRequest().getUrl().getPath()).isEqualTo("/app/polish.dude.eu/login");
    }

    @Order(3)
    @org.junit.jupiter.api.Test
    public void shouldLoginAsUserWithRoleAuthor() throws Exception {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'polish.dude.eu'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'dude'")).isEqualTo(1);

        HtmlForm resendForm = currentPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlButton sendButton = resendForm.getButtonByName("subButton");

        // when
        usernameField.setValueAttribute("dude");
        passwordField.setValueAttribute("pass");
        currentPage = sendButton.click();

        //then
        assertThat(currentPage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(currentPage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/polish.dude.eu/posts");
    }

    @Order(10)
    @Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = AFTER_TEST_METHOD)
    @org.junit.jupiter.api.Test
    public void dropData()
    {

    }

    //TODO Addedd test with ACL test cases

    @Override
    protected WebClient getWebClient() {
        return webClient;
    }

}