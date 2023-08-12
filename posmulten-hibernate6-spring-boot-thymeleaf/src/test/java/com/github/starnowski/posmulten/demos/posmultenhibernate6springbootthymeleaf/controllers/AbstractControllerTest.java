package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.controllers;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

public abstract class AbstractControllerTest {

    protected abstract WebClient getWebClient();

    protected void loginUserForDomain(String username, String domain) throws IOException {
        HtmlPage loginPage = getWebClient().getPage("/app/" + domain + "/home");
        HtmlForm resendForm = loginPage.getFormByName("loginForm");
        final HtmlTextInput usernameField = resendForm.getInputByName("username");
        final HtmlPasswordInput passwordField = resendForm.getInputByName("password");
        final HtmlButton sendButton = resendForm.getButtonByName("subButton");

        usernameField.setValueAttribute(username);
        passwordField.setValueAttribute("pass");
        sendButton.click();
    }

    protected void assertHttpResourceIsForbiddenForCurrentLoggedUser(String resourcePath) throws IOException {
        // when
        try {
            getWebClient().getPage(resourcePath);
            Assertions.fail("Response should contains 403 status");
        } catch (FailingHttpStatusCodeException exception) {
            // then
            assertThat(exception.getStatusCode()).isEqualTo(FORBIDDEN.value());
        }
    }

    protected void assertHttpResourceIsAvailableForCurrentLoggedUser(String resourcePath, String expectedHtmlTextPart) throws IOException {
        // when
        final HtmlPage htmlPage = getWebClient().getPage(resourcePath);

        // then
        assertThat(htmlPage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(htmlPage.getWebResponse().getContentAsString()).contains(expectedHtmlTextPart);
    }

    protected void assertHttpResourceIsAvailableForCurrentLoggedUserAndDoesNotDisplayExpectedContent(String resourcePath, String expectedHtmlTextPart) throws IOException {
        // when
        final HtmlPage htmlPage = getWebClient().getPage(resourcePath);

        // then
        assertThat(htmlPage.getWebResponse().getStatusCode()).isEqualTo(OK.value());
        assertThat(htmlPage.getWebResponse().getContentAsString()).doesNotContain(expectedHtmlTextPart);
    }
}
