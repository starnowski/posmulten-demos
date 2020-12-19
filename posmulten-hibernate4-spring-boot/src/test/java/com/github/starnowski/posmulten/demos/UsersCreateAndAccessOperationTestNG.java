package com.github.starnowski.posmulten.demos;

import com.github.starnowski.posmulten.demos.dto.PostDto;
import com.github.starnowski.posmulten.demos.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static com.github.starnowski.posmulten.demos.TestUtils.*;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_DATA_SOURCE;
import static com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
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
    public void prepareDatabase()
    {

    }

    @Test(dependsOnMethods = "prepareDatabase", dataProvider = "userData")
    public void createUser(UserDto user, String tenant)
    {
        // given
        assertThat(countUsersWithSpecifiedName(user.getUsername(), tenant)).isZero();
        String url = appTenantUrl(tenant, "users");

        // when
        ResponseEntity<UserDto> response = restTemplate.postForEntity(url, user, UserDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(countUsersWithSpecifiedName(user.getUsername(), tenant)).isEqualTo(1);
    }

    @Test(dependsOnMethods = "createUser", dataProvider = "userData")
    public void gettingAuthenticationAlertPageWhenAddingPostsResources(UserDto user, String tenant)
    {
        // given
        String url = appTenantUrl(tenant, "posts");
        String expectedText = "Post from " + user.getUsername();
        PostDto post = new PostDto().setText(expectedText);

        // when
        ResponseEntity<PostDto> response = restTemplate.postForEntity(url, post, PostDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NETWORK_AUTHENTICATION_REQUIRED);
    }

    @Test(dependsOnMethods = "createUser", dataProvider = "userData")
    public void loginWithBasicWhileAddingPostsResources(UserDto user, String tenant)
    {
        // given
        String url = appTenantUrl(tenant, "posts");
        String expectedText = "Post from " + user.getUsername();
        PostDto post = new PostDto().setText(expectedText);
        HttpHeaders headers = prepareBasicAuthorizationHeader(user.getUsername(), user.getPassword());
        RequestEntity<PostDto> requestEntity = RequestEntity.post(create(url)).header("Authorization", headers.getFirst("Authorization")).body(post, PostDto.class);

        // when
        ResponseEntity<PostDto> response = restTemplate.exchange(requestEntity, PostDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody().getText()).isEqualTo(post);
        assertThat(response.getBody().getAuthor()).isNotNull();
        assertThat(response.getBody().getAuthor().getUserId()).isEqualTo(user.getUserId());
    }

    @Test(dependsOnMethods = "loginWithBasicWhileAddingPostsResources", dataProvider = "userData")
    public void loginWithBasicWhileReadingAllTenantPostsResources(UserDto user, String tenant)
    {
        // given
        String url = appTenantUrl(tenant, "posts");
        HttpHeaders headers = prepareBasicAuthorizationHeader(user.getUsername(), user.getPassword());
        RequestEntity<Void> request = RequestEntity.get(create(url)).header("Authorization", headers.getFirst("Authorization")).build();

        // when
        ResponseEntity<PostsList> response = restTemplate.exchange(request, PostsList.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getPosts()).isNotEmpty().hasSize(1);
    }

    @Test(dependsOnMethods = {"createUser", "loginWithBasicWhileReadingAllTenantPostsResources", "loginWithBasicWhileReadingAllTenantPostsResources"}, alwaysRun = true)
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    public void deleteData()
    {

    }

    private int countUsersWithSpecifiedName(String name, String tenant)
    {
        return countRowsInTableWhere("user_info", "username = '" + name + "' AND tenant_id = '" + tenant + "'");
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(of = "id")
    @ToString
    private static class PostsList
    {
        private List<PostDto> posts = new ArrayList<>();
    }

    private HttpHeaders prepareBasicAuthorizationHeader(String username, String password)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        String plainClientCredentials= username + ":" + password;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        httpHeaders.add("Authorization", "Basic " + base64ClientCredentials);
        return httpHeaders;
    }
}
