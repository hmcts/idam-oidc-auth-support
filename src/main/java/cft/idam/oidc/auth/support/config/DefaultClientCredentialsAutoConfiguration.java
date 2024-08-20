package cft.idam.oidc.auth.support.config;

import cft.idam.oidc.auth.support.ClientCredentialsRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Default Client Credentials Auto Configuration. Uses the Spring Security OAuth2 Client Registrations
 * for the calls to perform the client credentials grant, so the registration-reference value needs to match
 * one of the oauth2 registrations defined in the spring security part of application.yaml.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "idam.oidc.client-credentials", name = "registration-reference")
public class DefaultClientCredentialsAutoConfiguration {

    @Value("${idam.oidc.client-credentials.registration-reference}")
    private String clientRegistrationReference;

    @Value("${idam.oidc.client-credentials.endpoint-regex}")
    private String clientCredentialsEndpointRegex;

    /**
     * Default client credentials feign request interceptor.
     *
     * @param oauth2AuthorizedClientService from spring
     * @param clientRegistrationRepository  from spring
     * @return Client credentials request interceptor.
     */
    @Bean
    public RequestInterceptor defaultClientCredentialsInterceptor(
            OAuth2AuthorizedClientService oauth2AuthorizedClientService,
            ClientRegistrationRepository clientRegistrationRepository) {
        log.info("idam-oidc-auth-support: Configured defaultClientCredentialsInterceptor "
                        + "for client reference: {}, endpoints: {}",
                clientRegistrationReference, clientCredentialsEndpointRegex);
        return new ClientCredentialsRequestInterceptor(
                clientRegistrationRepository.findByRegistrationId(clientRegistrationReference),
                openIdAuthorizedClientManager(oauth2AuthorizedClientService, clientRegistrationRepository),
                clientCredentialsEndpointRegex);
    }

    private OAuth2AuthorizedClientManager openIdAuthorizedClientManager(
            OAuth2AuthorizedClientService oauth2AuthorizedClientService,
            ClientRegistrationRepository clientRegistrationRepository) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                        oauth2AuthorizedClientService);
        authorizedClientManager
                .setAuthorizedClientProvider(
                        OAuth2AuthorizedClientProviderBuilder.builder()
                                .clientCredentials()
                                .refreshToken().build());
        return authorizedClientManager;
    }

}
