package cft.idam.oidc.auth.support;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * RequestInterceptor that uses spring security to retrieve a client credentials grant, and then adds the
 * bearer token to the feign calls.
 */
public class ClientCredentialsRequestInterceptor implements RequestInterceptor {

    private static final String AUTH_HEADER = "Authorization";

    private static final String BEARER = "Bearer";

    private final ClientRegistration clientRegistration;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final Pattern matchesPattern;

    private final Authentication principal;

    /**
     * Constructor.
     * @param clientRegistration client registration from spring.
     * @param authorizedClientManager authorized client manager from spring.
     * @param matchesRegex regex for urls that the bearer should be added to.
     */
    public ClientCredentialsRequestInterceptor(ClientRegistration clientRegistration,
            OAuth2AuthorizedClientManager authorizedClientManager,
            String matchesRegex) {
        this.clientRegistration = clientRegistration;
        this.authorizedClientManager = authorizedClientManager;
        this.principal = new ClientPrincipal(clientRegistration.getClientId());
        this.matchesPattern = Pattern.compile(matchesRegex);
    }

    @Override
    public void apply(RequestTemplate template) {
        if (handleUrl(template.url())) {
            addBearer(template, getAccessToken());
        }
    }

    private boolean handleUrl(String url) {
        return url != null && matchesPattern.matcher(url).find();
    }

    private void addBearer(RequestTemplate template, String token) {
        template.header(AUTH_HEADER, BEARER + " " + token);
    }

    private String getAccessToken() {
        OAuth2AuthorizedClient client = authorizedClientManager
                .authorize(OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId())
                        .principal(principal).build());
        if (isNull(client)) {
            throw new IllegalStateException("client credentials flow on " + clientRegistration
                    .getRegistrationId() + " failed, client is null");
        }
        return client.getAccessToken().getTokenValue();
    }

}