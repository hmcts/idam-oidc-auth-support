[![Release](https://jitpack.io/v/hmcts/idam-legacy-auth-support.svg)](https://jitpack.io/#hmcts/idam-oidc-auth-support)
[![JitPack Badge](https://github.com/hmcts/idam-legacy-auth-support/actions/workflows/jitpack_build.yml/badge.svg)](https://github.com/hmcts/idam-oidc-auth-support/actions/workflows/jitpack_build.yml)

# IdAM OIDC Auth Support
A Java module to simplify IdAM client credential calls.

## User Guide

This is a library to support OpenId client credential token calls for CFT services. It provides:

* Feign interceptor to add a client credential bearer token to backend calls.
* Tokens from Hmcts-access are managed by spring security

The library is only required for making API calls to other services. It is not required for authentication of users with Hmcts-access.

Once you have integrated with spring security you can use scope annotations to control access to endpoints.

The library has auto configuration enabled based on spring properties, so all you need to do to start using it is import the library into
your spring boot project and apply the correct configuration as described below.

### Spring Client Credentials

To add client credential bearer tokens to feign calls in your project set the following values in application.yaml

```
idam:
  oidc:
    client-credentials:
      registration-reference: my-example-service
      endpoint-regex: /my-url.*
```

The registration reference refers to the spring security oauth2 client that you will also need to configure, for example:
```
spring:
  security:
      client:
        registration:
          my-example-service:
            authorization-grant-type: client_credentials
            client-id: my-service-client-id
            client-secret: my-service-client-secret
            client-authentication-method: client_secret_post
            redirect-uri: https://my-service-redirect
            scope:
              - example-scope
```

Once that is done then any feign calls that you make that match the `endpoint-regex` will have an Authentication header with the
client credentials bearer token. The fetching/caching of the token is handled by spring security.

When the client credentials support is active you will see a message similar to the following on application startup:

```
DefaultClientCredentialsAutoConfiguration idam-oidc-auth-support: Configured defaultClientCredentialsInterceptor for client reference:  my-example-service, endpoints: /my-url.*
```

### Making Client Credentials calls in tests

This library is not intended to be used for fetching client credential token calls in tests. The simplest way to make these token calls
in tests is to use SerenityRest.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.