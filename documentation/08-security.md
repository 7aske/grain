# Security

By default, security is disabled and can be enabled with `SECURITY_ENABLED`
configuration setting.

Security implementation from the framework user's standpoint relies on these
following concepts(interfaces):

* `Authentication`
* `SecurityContextHolder`
* `Authority`
* `User`
* `PasswordEncoder`

And for the extensibility and reconfiguration:

* `HttpRequestAuthenticationProviderStrategy`
* `AuthenticationManager`
* ~~AuthorizationManager~~
* `AuthenticationEntryPoint`
* `SecurityConfigurer`
* `UserService`
* `SecurityHandlerProxy`

## Authentication

`Authentication` interface describes how user information should be presented in
the context of the security pipeline. Default implementation of this interface
is `BasicAuthentication`.
`Authentication` contains all the required information to validate and authorize
user requests and should be available from the `SecurityContextHolder` class.

## Security Context Holder

`SecurityContextHolder` stores the `SecurityContext` for the current request.
This is managed by default with the `SecurityContextHolderStrategy` which is
a `ThreadLocalSecurityContextStrategy` which stores the `SecurityContext`
information in a `TheradLocal` which is perfectly valid since every request is
its own thread. Calling `SecurityContextHolder#getContext` and
then `getAuthentication` on the resulting object we get the current
authentication for the request.

## Authority

`Authority` represents a permission or a role in the system. Authorities are
differentiated by its name. Default `Authority` implementation is
a `BasicAuthority` class which only stores the name of the current authority.

## User

`User` interface represents the functionality required for the user object in
the system to be properly authenticated and authorized. It specifies some
functionalities such as getting the user's username and password, getting their
authorities and such.

## PasswordEncoder

`PasswordEncoder` is responsible for encoding the user's password and verifying
if, during authentication, authentication user's password is matching the stored
hash. Default implementation is using `MessageDigest.getInstance("SHA-256")`.

## Http Request Authentication Provider Strategy

`HttpRequestAuthenticationProviderStrategy` defines how the `Authentication` is
extracted from the current request and in turn from the current session.

## Authentication Manager

`AuthenticationManager` handles the validation of the current `Authentication`.

## Authentication Entry Point

`AuthenticationEntryPoint` is the initial class where the login credentials are
parsed and converted, if valid, to an `Authentication` object which is then set
to the `SecurityContext`. Default implementation consists of
a `FormLoginAuthenticationEntryPoint`
and `FormLoginAuthenticationEntryPointController`. Entry point controller
accepts a `application/x-www-form-urlencoded` request from which `username`
and `password` values are extracted and used to form a valid authentication by
validating them against a `PasswordEncoder` and a `User` fetched
from `UserService`.

## Security Configurer

Overriding `SecurityConfigurer` framework user is able to define authentication
rules required for any endpoint in the system.

Here we can see the default security configuration:

```java
/**
 * Default SecurityConfigurer
 */
@Grain @Default final class DefaultSecurityConfigurer implements SecurityConfigurer {
	private final Logger logger = LoggerFactory.getLogger(DefaultSecurityConfigurer.class);

	@Override
	public void configure(SecurityConfigurationBuilder builder) {
		String username = "root";
		String password = UUID.randomUUID().toString();

		builder.withRules()
				.urlPattern("/**").authenticated().and()
				.urlPattern("/login").unauthenticated().method(HttpMethod.GET, HttpMethod.POST).and()
				.urlPattern("/logout").unauthenticated().method(HttpMethod.GET).and()
				.buildRules()
				.withDefaultUser()
				.username(username)
				.password(password)
				.buildDefaultUser();

		logger.info("Created default user with username: {} and password: {}", username, password);
	}
}
```

## User Service

`UserService` is a built-in Grain used for fetching the User object. By
default, `UserService` will just return the default user specified in the
default security configuration.

```java
@Grain
@Default
public class UserServiceImpl implements UserService {
	@Inject
	private SecurityConfiguration configuration;

	@Override
	public User findByUsername(String username) throws UserNotFoundException {
		return configuration.getDefaultUser();
	}
}
```

## Security Handler Proxy

`RequestHandlerProxy` is a Grain interface responsible for adding verification
or some other logic to every request. `SecurityHandlerProxy` is verifying
whether the request satisfies rules defined for the endpoint it is targeting. By
default, if the security configuration is disabled this Grain is not loaded and its
implementation is replaced by `DefaultHandlerProxy` which is only delegating
requests to the handler without any additional logic.

## Login page 

There is a default login page which is defined as Grain, and it can be redefined
by the user or the user can just define a controller endpoint which covers
the `/login` path and framework will take priority of user defined classes when
resolving dependency injections.

```java
@Grain
public class DefaultLoginPage implements LoginPage {
  public String getContent() {
    return "<!DOCTYPE html>" + ... 
  }
}
```