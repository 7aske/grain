# Grain

## Description

Zero dependency web framework inspired by Spring.

This is a pet project to showcase programming paradigms, stereotypes and
patterns. Tries to provide all functionalities required to build an MVC or
Service-Oriented web application.

DO NOT USE IN PRODUCTION!

## Functionality

* Java based configuration
* HTTP
    * Request
    * Response
    * Session
    * Cookies
    * Headers
    * JSON serialization/deserialization
* Multithreaded request handling
* Component system
    * Dependency injection
        * Lifecycle hooks (AfterInit)
        * Conditional loading
        * Field and annotation based injection
    * Middleware
    * Controllers
        * Request parameter binding
        * Path variable binding
        * Method request mapping
        * Converters
* Database
    * Connection pooling
    * Model based entity system
    * Annotation based model configuration
* View templating
    * JSP-like scriptlets
    * Compatible with Java
    *
    * Fragments (reusable HTML components)
    * Form binding to controller method arguments
* Static directory serving
    * Classpath and non-classpath directories
* Security
    * Java configuration
    * Default implementation uses HTTP Cookies for tracking
    * Default login page and user
    * Roles
* Logging system
* Custom generated error pages

## Quickstart

```java
public class BlogApp extends GrainApp {

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

  @Override
  protected void configure(ConfigurationBuilder builder) {
    builder.setProperty(DATABASE_USER, "root");
    builder.setProperty(DATABASE_PASS, "toor");
    builder.setProperty(DATABASE_NAME, "blog");
    builder.setProperty(DATABASE_HOST, "127.0.0.1");
    builder.setProperty(DATABASE_PORT, 3306);
    builder.setProperty(DATABASE_DRIVER_CLASS, "com.mysql.jdbc.Driver");
  }
  
  public static void main(String[] args) {
    GrainAppRunner.run(BlogApp.class);
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