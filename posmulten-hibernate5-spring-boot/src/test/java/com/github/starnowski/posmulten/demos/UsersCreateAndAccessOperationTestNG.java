package com.github.starnowski.posmulten.demos;

import com.github.starnowski.posmulten.demos.dto.PostDto;
import com.github.starnowski.posmulten.demos.dto.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
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
                {new UserDto().setUsername("johndoe").setPassword(PASSWORD), new TenantTestData().setTenant("ten1").setDifferentTenant("xds")},
                {new UserDto().setUsername("marydoe").setPassword(PASSWORD), new TenantTestData().setTenant("xds").setDifferentTenant("ten1")}
        };
    }

    @BeforeMethod
    public void setUp()
    {
        this.setDataSource(ownerDataSource);
    }

    @Test
    @Sql(value = {CLEAR_DATABASE_SCRIPT_PATH, INSERT_TESTS_TENANTS_SCRIPT_PATH},
            config = @SqlConfig(transactionMode = ISOLATED, dataSource = OWNER_DATA_SOURCE, transactionManager = OWNER_TRANSACTION_MANAGER),
            executionPhase = BEFORE_TEST_METHOD)
    public void prepareDatabase()
    {

    }

    @Test(dependsOnMethods = "prepareDatabase", dataProvider = "userData")
    public void createUser(UserDto user, TenantTestData tenantTestData)
    {
        // given
        assertThat(countUsersWithSpecifiedName(user.getUsername(), tenantTestData.getTenant())).isZero();
        String url = appTenantUrl(tenantTestData.getTenant(), "users");

        // when
        ResponseEntity<UserDto> response = restTemplate.postForEntity(url, user, UserDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(countUsersWithSpecifiedName(user.getUsername(), tenantTestData.getTenant())).isEqualTo(1);
    }

    @Test(dependsOnMethods = "createUser", dataProvider = "userData")
    public void gettingAuthenticationAlertPageWhenAddingPostsResources(UserDto user, TenantTestData tenantTestData)
    {
        // given
        String url = appTenantUrl(tenantTestData.getTenant(), "posts");
        String expectedText = "Post from " + user.getUsername();
        PostDto post = new PostDto().setText(expectedText);

        // when
        ResponseEntity<PostDto> response = restTemplate.postForEntity(url, post, PostDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test(dependsOnMethods = "createUser", dataProvider = "userData")
    public void loginWithBasicWhileAddingPostsResources(UserDto user, TenantTestData tenantTestData)
    {
        // given
        String url = appTenantUrl(tenantTestData.getTenant(), "posts");
        String expectedText = "Post from " + user.getUsername();
        PostDto post = new PostDto().setText(expectedText);
        HttpHeaders headers = prepareBasicAuthorizationHeader(user.getUsername(), user.getPassword());
        RequestEntity<PostDto> requestEntity = RequestEntity.post(create(url)).header("Authorization", headers.getFirst("Authorization")).body(post, PostDto.class);

        // when
        ResponseEntity<PostDto> response = restTemplate.exchange(requestEntity, PostDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody().getText()).isEqualTo(post.getText());
        assertThat(response.getBody().getAuthor()).isNotNull();
        assertThat(response.getBody().getAuthor().getUserId()).isNotNull();
    }

    @Test(dependsOnMethods = "loginWithBasicWhileAddingPostsResources", dataProvider = "userData")
    public void loginWithBasicWhileReadingAllTenantPostsResources(UserDto user, TenantTestData tenantTestData)
    {
        // given
        String url = appTenantUrl(tenantTestData.getTenant(), "posts");
        HttpHeaders headers = prepareBasicAuthorizationHeader(user.getUsername(), user.getPassword());
        RequestEntity<Void> request = RequestEntity.get(create(url)).header("Authorization", headers.getFirst("Authorization")).build();
        ParameterizedTypeReference<List<PostDto>> typeReference = new ParameterizedTypeReference<List<PostDto>>() {};

        // when
        ResponseEntity<List<PostDto>> response = restTemplate.exchange(request, typeReference);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotEmpty().hasSize(1);
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

    private HttpHeaders prepareBasicAuthorizationHeader(String username, String password)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        String plainClientCredentials= username + ":" + password;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        httpHeaders.add("Authorization", "Basic " + base64ClientCredentials);
        return httpHeaders;
    }

    @Data
    @Accessors(chain = true)
    private static class TenantTestData
    {
        private String tenant;
        private String differentTenant;
    }
}
