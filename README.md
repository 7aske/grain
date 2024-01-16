# Grain

## Description

Zero dependency web framework inspired by Spring.

This is a pet project to showcase programming paradigms, stereotypes and
patterns. Tries to provide all functionalities required to build an MVC or
Service-Oriented web application.

## Functionality

* Java based configuration
* HTTP
    * Request
    * Response
    * Session
    * Cookies
    * Headers
    * JSON serialization/deserialization
    * Closely follows the Servlet API
* Multithreaded request handling
* Component system
    * Dependency injection
        * Lifecycle hooks (AfterInit)
        * Conditional loading
        * Field and annotation based injection
        * Bean-like(Spring) methods
    * Middleware (filters)
    * Controllers
        * Request parameter binding
        * Path variable binding
        * Method request mapping
        * Converters
        * Response writers
* ORM
    * Connection pooling
    * Model based entity system
    * Annotation based model configuration
* View templating
    * JSP-like template engine
    * Compatible with Java
    * Fragments (reusable HTML components)
    * Form binding to controller method arguments
* Static directory serving
    * Classpath and non-classpath directories
* Security
    * Java configuration
    * Default implementation uses HTTP Cookies for session tracking
    * Default login page and user
    * Roles
* Logging system
* Custom generated error pages
* Starters for extending functionality
    * Tomcat integration

## Quickstart

Framework configuration is all handled in the `parent` module. You must add it as a parent of your project as well.

```xml
...

<parent>
    <groupId>com._7aske.grain</groupId>
    <artifactId>parent</artifactId>
    <version>1.1.0</version>
</parent>

<repositories>
    <repository>
        <id>7aske-repository-releases</id>
        <name>7aske repository</name>
        <url>https://maven.7aske.xyz/releases</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com._7aske.grain</groupId>
        <artifactId>web</artifactId>
    </dependency>
    ...
</dependencies>
...
```
Following this instruction will allow you to build a "fat" jar that can be executed with `java -jar yourapp.jar`.

```java
@GrainApplication
public class BlogApp {

  @Table(name = "post")
  static final class Post extends Model {
    @Id
    @Column(name = "post_id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "body")
    private String body;
  }

  @Controller
  @RequestMapping("/posts")
  static final class PostController {
    @GetMapping
    public JsonResponse<List<Post>> getPosts() {
      return JsonResponse.ok(Post.findAll(Post.class));
    }
  }

  @Grain
  public Configuration configuration(Configuration config) {
    config.set("database.user", "root");
    config.set("database.pass", "toor");
    config.set("database.name", "blog");
    config.set("database.host", "127.0.0.1");
    config.set("database.port", 3306);
    config.set("database.driver_class", "com.mysql.jdbc.Driver");
    return config;
  }

  public static void main(String[] args) {
    GrainAppRunner.run(BlogApp.class);
  }
}
```

Grain does not have a very broad nor complex set of features, but it is very extensible as is any DI system. You can
add your own components and extend the framework to your needs. In the following example we have integrated Hibernate
into our application using only one Grain component.

```java

@Grain
public class HibernateConfiguration {

	@Grain
	public SessionFactory sessionFactory() {
		Configuration configuration = new Configuration();

		GrainClassLoader grainClassLoader = new GrainJarClassLoader(CinemaApp.class.getPackageName());
		grainClassLoader.loadClasses(cl -> cl.isAnnotationPresent(Entity.class))
				.forEach(configuration::addAnnotatedClass);

		return configuration.buildSessionFactory();
	}
}

```

## Building

Build the source

```
mvn package
```

This gives you a grainXXX.jar file that you can add to your project.

## Examples

See [documentation](./documentation/README.md) for more information.

## Contact

Nikola Tasić – nik@7aske.com

Distributed under the GPL v2 license. See ``LICENSE`` for more information.

[7aske.com](https://7aske.com)

[github.com/7aske](https://github.com/7aske)

## Contributing

1. Fork it (<https://github.com/7aske/grain/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request