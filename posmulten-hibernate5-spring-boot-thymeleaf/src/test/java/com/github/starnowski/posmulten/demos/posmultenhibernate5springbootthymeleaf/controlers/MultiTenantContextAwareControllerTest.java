package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controlers;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, MULTI_TENANT_CONTEXT_AWARE_CONTROLLER_TEST_SCRIPT_PATH},
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = BEFORE_TEST_METHOD)
@Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = AFTER_TEST_METHOD)
public class MultiTenantContextAwareControllerTest {

    @Autowired
    WebClient webClient;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        System.out.println("encrypted password:" + bCryptPasswordEncoder.encode("pass"));
        webClient.getCookieManager().clearCookies();
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    @Test
    public void shouldRedirectUserToCorrectLoginUrlAfterTryingToReachSecuredResource() throws Exception {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);

        // when
        HtmlPage homePage = this.webClient.getPage("/app/my.doc.com/home");
        WebResponse response = homePage.getWebResponse();
        int responseStatus = response.getStatusCode();

        //http://localhost:8080/app/\{tenantDomain\}/login

        // then
        assertThat(responseStatus).isEqualTo(OK.value());
        assertThat(response.getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/login");
    }

    //http://localhost:8080/app/my.doc.com/login


    @Test
    public void shouldDisplayLoginPageForExistedDomain() throws Exception {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);

        // when
        HtmlPage homePage = this.webClient.getPage("/app/my.doc.com/login");
        WebResponse response = homePage.getWebResponse();
        int responseStatus = response.getStatusCode();

        // then
        assertThat(responseStatus).isEqualTo(OK.value());
    }

    @Test
    public void shouldLoginAsUserExistedInCurrentDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'starnowski'")).isEqualTo(1);

        HtmlPage loginPage = this.webClient.getPage("/app/my.doc.com/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlInput sendButton = resendForm.getInputByName("subButton");

        // when
        usernameField.setValueAttribute("starnowski");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();

        //then
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/home");
    }

    @Test
    public void shouldReturnForbiddenErrorPageForLoggedUserFromOtherDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'polish.dude.eu'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'my.doc.com'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'mcaine'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'mcaine'")).isEqualTo(1);

        HtmlPage loginPage = this.webClient.getPage("/app/polish.dude.eu/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlInput sendButton = resendForm.getInputByName("subButton");
        usernameField.setValueAttribute("mcaine");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/polish.dude.eu/home");

        // when
        try {
            this.webClient.getPage("/app/my.doc.com/home");
            Assert.fail("Response should contains 403 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(FORBIDDEN.value());
        }

    }

    @Test
    public void shouldDisplayPageWithStatusNotFoundForNoneExistDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "domain = 'no.such.com'")).isEqualTo(0);

        // when
        try {
            this.webClient.getPage("/app/no.such.com/home");
            Assert.fail("Response should contains 401 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(NOT_FOUND.value());
        }
    }

    @Test
    public void shouldFailToLoginUserFromOtherDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'polish.dude.eu'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'my.doc.com'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'starnowski'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = '/mcaine'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = '/mcaine'")).isEqualTo(1);

        HtmlPage loginPage = this.webClient.getPage("/app/my.doc.com/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlInput sendButton = resendForm.getInputByName("subButton");

        // when
        usernameField.setValueAttribute("/mcaine");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();

        //then
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/login");
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getQuery()).contains("error");
    }

    @Test
    public void shouldBeForbiddenAuditorAndAdminResourcesForUserWithoutAnyRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = '/mcaine' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'")).isEqualTo(0);

        // when
        loginUserForDomain("/mcaine", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/assessments");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/config");
    }

    @Test
    public void shouldBeForbiddenAdminResourcesForUserWithAuditorRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'dude' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15' and role='AUDITOR'")).isEqualTo(1);

        // when
        loginUserForDomain("dude", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/assessments", "Page for assessments!");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/config");
    }

    @Test
    public void shouldBeAvailableAllResourcesForUserWithAdminRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'starnowski' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14' and role='ADMIN'")).isEqualTo(1);

        // when
        loginUserForDomain("starnowski", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/assessments", "Page for assessments!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/config", "Page for config");
    }

    //TODO Addedd test with ACL test cases

    private void loginUserForDomain(String username, String domain) throws IOException {
        HtmlPage loginPage = this.webClient.getPage("/app/" + domain + "/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlInput sendButton = resendForm.getInputByName("subButton");

        usernameField.setValueAttribute(username);
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();
    }

    private void assertHttpResourceIsForbiddenForCurrentLoggedUser(String resourcePath) throws IOException {
        // when
        try {
            this.webClient.getPage(resourcePath);
            Assert.fail("Response should contains 403 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(FORBIDDEN.value());
        }
    }

    private void assertHttpResourceIsAvailableForCurrentLoggedUser(String resourcePath, String expectedHtmlTextPart) throws IOException {
        // when
        final HtmlPage htmlPage = this.webClient.getPage(resourcePath);

        // then
        assertThat(htmlPage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(htmlPage.getWebResponse().getContentAsString()).contains(expectedHtmlTextPart);
    }

}