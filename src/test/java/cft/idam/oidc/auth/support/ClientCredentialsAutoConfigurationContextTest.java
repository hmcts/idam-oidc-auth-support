package cft.idam.oidc.auth.support;

import feign.RequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest(
        classes = ClientCredentialsAutoConfigurationContextTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
            "idam.oidc.client-credentials.registration-reference=test-client",
            "idam.oidc.client-credentials.endpoint-regex=/private/.*",
            "spring.security.oauth2.client.registration.test-client.authorization-grant-type=client_credentials",
            "spring.security.oauth2.client.registration.test-client.client-id=test-client-id",
            "spring.security.oauth2.client.registration.test-client.client-secret=test-client-secret",
            "spring.security.oauth2.client.provider.test-client.token-uri=https://idam.test/oauth2/token"
        })
class ClientCredentialsAutoConfigurationContextTest {

    @Autowired
    private RequestInterceptor requestInterceptor;

    @Test
    void createsClientCredentialsRequestInterceptor() {
        assertInstanceOf(ClientCredentialsRequestInterceptor.class, requestInterceptor,
                "The client credentials interceptor should be auto-configured");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }
}
