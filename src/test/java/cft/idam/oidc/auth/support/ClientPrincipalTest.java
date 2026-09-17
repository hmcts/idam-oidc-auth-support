package cft.idam.oidc.auth.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


class ClientPrincipalTest {

    @Test
    void testGetName() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertEquals("test-client-id", underTest.getName(), "The principal should expose its client ID as its name");
    }

    @Test
    void hasNoPrincipal() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertNull(underTest.getPrincipal(), "A client principal should not expose another principal");
    }

    @Test
    void hasNoCredentials() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertNull(underTest.getCredentials(), "Credentials should not be retained by the principal");
    }

    @Test
    void hasNoDetails() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertNull(underTest.getDetails(), "A client principal should not expose authentication details");
    }

    @Test
    void hasNoAuthorities() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertEquals(0, underTest.getAuthorities().size(), "A client principal should not have user authorities");
    }

    @Test
    void isNotAuthenticated() {
        ClientPrincipal underTest = new ClientPrincipal("test-client-id");
        assertFalse(underTest.isAuthenticated(), "The principal is an authorization request identity only");
    }
}
