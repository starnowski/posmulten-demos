package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, MULTI_TENANT_CONTEXT_AWARE_CONTROLLER_TEST_SCRIPT_PATH},
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = BEFORE_TEST_METHOD)
@Sql(value = CLEAR_DATABASE_SCRIPT_PATH,
        config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
        executionPhase = AFTER_TEST_METHOD)
public class MultiTenantContextAwareControllerTest extends AbstractControllerTest{

    private static Stream<Arguments> provideLoggedUserAndExpectedVisiblePost() {
        return Stream.of(
                Arguments.of("polish.dude.eu", "starnowski", "First post in application for xds1"),
                Arguments.of("polish.dude.eu", "dude", "Second post in application for xds1"),
                Arguments.of("my.doc.com", "dude", "Post post and post"),
                Arguments.of("my.doc.com", "starnowski", "This is a text content")
        );
    }

    private static Stream<Arguments> provideLoggedUserAndExpectedNoneVisiblePost() {
        return Stream.of(
                Arguments.of("polish.dude.eu", "starnowski", "Post post and post"),
                Arguments.of("polish.dude.eu", "dude", "This is a text content"),
                Arguments.of("my.doc.com", "dude", "First post in application for xds1"),
                Arguments.of("my.doc.com", "starnowski", "Second post in application for xds1")
        );
    }

    @Autowired
    WebClient webClient;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        System.out.println("encrypted password:" + bCryptPasswordEncoder.encode("pass"));
        webClient.getCookieManager().clearCookies();
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @org.junit.jupiter.api.Test
    public void shouldRedirectUserToCorrectLoginUrlAfterTryingToReachSecuredResource() throws Exception {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);

        // when
        HtmlPage homePage = this.webClient.getPage("/app/my.doc.com/home");
        WebResponse response = homePage.getWebResponse();
        int responseStatus = response.getStatusCode();

        // then
        assertThat(responseStatus).isEqualTo(OK.value());
        assertThat(response.getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/login");
    }


    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
    public void shouldLoginAsUserExistedInCurrentDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'starnowski'")).isEqualTo(1);

        HtmlPage loginPage = this.webClient.getPage("/app/my.doc.com/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlButton sendButton = resendForm.getButtonByName("subButton");

        // when
        usernameField.setValueAttribute("starnowski");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();

        //then
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/home");
    }

    @org.junit.jupiter.api.Test
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
        final HtmlButton sendButton = resendForm.getButtonByName("subButton");
        usernameField.setValueAttribute("mcaine");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/polish.dude.eu/home");

        // when
        try {
            this.webClient.getPage("/app/my.doc.com/home");
            Assertions.fail("Response should contains 403 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(FORBIDDEN.value());
        }

    }

    @org.junit.jupiter.api.Test
    public void shouldDisplayPageWithStatusNotFoundForNoneExistDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "domain = 'no.such.com'")).isEqualTo(0);

        // when
        try {
            this.webClient.getPage("/app/no.such.com/home");
            Assertions.fail("Response should contains 401 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(NOT_FOUND.value());
        }
    }

    @org.junit.jupiter.api.Test
    public void shouldFailToLoginUserFromOtherDomain() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds' and domain = 'my.doc.com'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'polish.dude.eu'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "tenant_info", "tenant_id = 'xds1' and domain = 'my.doc.com'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'starnowski'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds' and username = 'mcaine'")).isEqualTo(0);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'mcaine'")).isEqualTo(1);

        HtmlPage loginPage = this.webClient.getPage("/app/my.doc.com/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlButton sendButton = resendForm.getButtonByName("subButton");

        // when
        usernameField.setValueAttribute("mcaine");
        passwordField.setValueAttribute("pass");
        final HtmlPage homePage = sendButton.click();

        //then
        assertThat(homePage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getPath()).isEqualTo("/app/my.doc.com/login");
        assertThat(homePage.getWebResponse().getWebRequest().getUrl().getQuery()).contains("error");
    }

    @org.junit.jupiter.api.Test
    public void shouldBeForbiddenAuthorAndAdminResourcesForUserWithoutAnyRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'mcaine' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'")).isEqualTo(0);

        // when
        loginUserForDomain("mcaine", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/posts");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/posts/");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/config");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/config/");
    }

    @org.junit.jupiter.api.Test
    public void shouldBeForbiddenAdminResourcesForUserWithAuthorRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'dude' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15' and role='AUTHOR'")).isEqualTo(1);

        // when
        loginUserForDomain("dude", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/posts", "Page for posts!");
        assertHttpResourceIsForbiddenForCurrentLoggedUser("/app/polish.dude.eu/config");
    }

    @org.junit.jupiter.api.Test
    public void shouldBeAvailableAllResourcesForUserWithAdminRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'starnowski' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14' and role='ADMIN'")).isEqualTo(1);

        // when
        loginUserForDomain("starnowski", "polish.dude.eu");

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/hello", "Hello World!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/posts", "Page for posts!");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/config", "Page for config");
    }

    @org.junit.jupiter.api.Test
    public void shouldDisplayAllUsersInTableForUserWithAdminRole() throws IOException {
        // given
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_info", "tenant_id = 'xds1' and username = 'starnowski' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'")).isEqualTo(1);
        assertThat(countNumberOfRecordsWhere(jdbcTemplate, "user_role", "tenant_id = 'xds1' and user_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14' and role='ADMIN'")).isEqualTo(1);

        // when
        loginUserForDomain("starnowski", "polish.dude.eu");

        // then
        // a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "mcaine");

        // a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "starnowski");

        // a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15");
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/polish.dude.eu/users", "dude");
    }

    @ParameterizedTest
    @MethodSource("provideLoggedUserAndExpectedVisiblePost")
    public void shouldDisplayPostWithExpectedText(String domain, String user, String  expectedText) throws IOException {
        // when
        loginUserForDomain(user, domain);

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUser("/app/" + domain + "/posts", expectedText);
    }

    @ParameterizedTest
    @MethodSource("provideLoggedUserAndExpectedNoneVisiblePost")
    public void shouldDisplayPostButWithoutExpectedText(String domain, String user, String  expectedText) throws IOException {
        // when
        loginUserForDomain(user, domain);

        // then
        assertHttpResourceIsAvailableForCurrentLoggedUserAndDoesNotDisplayExpectedContent("/app/" + domain + "/posts", expectedText);
    }

    //TODO Addedd test with ACL test cases

    @Override
    protected WebClient getWebClient() {
        return webClient;
    }

}