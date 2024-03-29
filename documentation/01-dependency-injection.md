# Dependency injection

Core of the Grain framework is its dependency injection system. It allows instances
of Grain components to be referenced across other components and specified in declarative
fashion. Dependency injection system creates instances and injects them with
their dependencies. This helps the user by removing the need of manually
instantiating classes.

## Component registration

A class can be marked as Grain dependency injection component simply by annotating
it by `@Grain` annotation:

```java
@Grain
public class UserServiceImpl implements UserService {
  @Override
  public User findByUsername(String s) throws UserNotFoundException {
    return User.findByUsername(s);
  }
}
```

This is simple component providing functionality of fetching the user from
the database. We can of course reference Grains by their implemented interface
which we will show in the following example.

## Component injection

### Field injection

```java
@Grain
public class FormLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Inject
  private UserService userService;

  @Override
  public Authentication authenticate(HttpRequest request, HttpResponse response) throws GrainSecurityException {
    // implementation details ...
  }
}
```

In the code above we can see another Grain component referencing the declared
UserServiceImpl by its interface. This allows us to declare other classes that
implement the same interface and override the default implementation of the component.
Dependency injection is used heavily internally by the Grain framework and this
proves to be very useful when we want to extend or override the default functionality
of its systems.

In this example the dependency is injected using field injection
mechanism. Fields in the class annotated with `@Inject` annotation will be considered
in the injection lifecycle and their dependencies will be injected if found. Otherwise,
an error will be thrown.

### Constructor injection

Injection can be established also by using constructor parameters.

```java
@Controller
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
}
```

Here the framework will try to find all Grains specified in the constructor and try
to inject them when creating the instance. All constructor parameters are assumed
to be valid Grains and if the dependencies are not satisfied an exception will
be thrown.

### Lifecycle injection

Another way of injection can be preformed by referencing Grains as parameters of
lifecycle hooks. This is not recommended as lifecycle hooks are not called until
the end of dependency resolving cycle and therefore fields referencing other Grains 
might be null at the moment of creation of the instance. This can be useful on the other
hand when preforming configuration alteration since any possible values that
need to fetched from other Grains are sure to be initialized.

```java
@Controller
@RequestMapping("/users")
public class UserController {
  private UserService userService;

  // NOT RECOMMENDED
  @AfterInit
  private void setup(UserService userService) {
    this.userService = userService;
  }
}
```

Configuration example:

```java
@Grain
public class Configuration {
	@AfterInit
	public void setup(com._7aske.grain.core.configuration.Configuration configuration) {
		configuration.setPropertyUnsafe("application.name", "grain");
	}
}
```

## Grain methods

### Hibernate integration

Grain methods are basically factory methods for components that are not a part of
the user's source code. They are similar to `@Bean` annotated methods you would
find in spring projects.

```java
@Grain
public class HibernateConfiguration {

	@Grain
	public SessionFactory sessionFactory() {
		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

		GrainClassLoader grainClassLoader = new GrainJarClassLoader(CinemaApp.class.getPackageName());
		grainClassLoader.loadClasses(cl -> cl.isAnnotationPresent(Entity.class))
				.forEach(configuration::addAnnotatedClass);

		return configuration.buildSessionFactory();
	}
}
```

This example shows all the code needed to integrate Hibernate in a Grain framework
project. Rest of the configuration can be placed in `application.properties` file:

```properties
hibernate.dialect=org.hibernate.dialect.MariaDBDialect
hibernate.connection.driver_class=org.mariadb.jdbc.Driver
hibernate.connection.url=jdbc:mariadb://localhost:3306/example
hibernate.connection.username=root
hibernate.connection.password=
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
```

### Thymeleaf integration

Another example can be integration of the Thymeleaf templating library to be used
alongside the existing GTL engine. As GTL template resolver targets only files 
with the `.gtl` extension thymeleaf can be seamlessly integrated as this configuration
only resolves `.html` files as Thymeleaf templates.

```java
@Grain
public class ThymeleafConfig {

	private ITemplateResolver htmlTemplateResolver() {
		StringTemplateResolver resolver
				= new StringTemplateResolver();
		resolver.setCacheable(false);
		return resolver;
	}

	@Grain
	public ViewResolver thymeleafViewResolver(Configuration configuration) {
		return new ViewResolver() {
			@Override
			public boolean supports(View view) {
				return view.getName().endsWith(".html");
			}

			@Override
			public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
				populateImplicitObjects(view, request, response, session, authentication, configuration);

				Context context = new Context();
				context.setVariables(view.getAttributes());

				TemplateEngine templateEngine = new TemplateEngine();
				templateEngine.setTemplateResolver(ViewResolverConfig.this.htmlTemplateResolver());

		        String body = templateEngine.process(view.getName(), context);

				try {
					response.getOutputStream().write(body.getBytes());
					response.setHeader(CONTENT_TYPE, view.getContentType());
				} catch (IOException e) {
					throw new GrainRuntimeException(e);
				}
			}
		};
	}
}
```

## Lifecycle hooks

Currently, there is only one lifecycle hook - `@AfterInit` which is called after
the dependency injection cycle after all Grains' dependencies are resolved and injected.
Method declared as an `@AfterInit` method can have other Grains as its parameters
and those Grains will be injected before calling the method. An example of this
hook is available [here](#lifecycle-injection).

## Value reference

Grain class fields can have their values injected directly from configuration
or from other Grain's fields. This can be achieved by using the `@Value` annotation.

```java
@Grain
public class JwtProvider { 
  @Value("jwt.secret")
  private String jwtSecret;
  
  // implementation ...
}
```

In this example `jwtSecret` field is being injected with the value from the configuration
Grain with the key `jwt.secret`. Note that the fields are populated after resolving
all dependencies and before calling lifecycle hooks.

Also `@Value` annotation can reference other Grains and even call their methods.

```java
@Grain
public class TestGrain {
  private Integer answer = 42;

  public String getValue() {
    return "value";
  }
}

@Grain
public class TestGrainReferenceGrain {
  @Value("testGrain.answer")
  private Integer answer;
  @Value("testGrain.getValue()")
  private String value;
}
```

`@Value` annotation can contain expressions parsed by the GTL interpreter.

```java
@Grain
public class TestGrainReferenceGrain {
  @Value("grain.server.port < 1000")
  private boolean requiresElevation;
}
```

## Conditional loading

Grains can be conditionally loaded(registered) for injection using the `@Condition`
annotation. Usage is similar to `@Value` - its value is evaluated as a boolean expression
in the GTL interpreter and all the same rules apply but in the end if the expression
is truthy the Grain is loaded.

```java
@Grain
@Condition("grain.security.enabled")
public class SecurityHandlerProxyFactory implements HandlerProxyFactory {
  // implementation ...    
}
```

## Component ordering

Components can be injected as lists as well. In that case `@Order` annotation can
be used to specify the order in which the components will appear in the list.

This annotation can also be used to order `Middleware` components and specify the
order in which they will be executed.
