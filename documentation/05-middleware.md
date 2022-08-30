# Middleware

In Grain Middlewares are classes that preform additional logic for each HTTP request.
Middlewares return a boolean value. If the returned value is true Handler chain is
stopped and the response is returned to the user. Middlewares must be annotated
by the `@Grain` annotation otherwise it will not be picked up by the dependency 
injection system.

## Code example

```java
@Grain
@Order(10)
public class PoweredByMiddleware implements Middleware {
  @Override
  public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
    httpResponse.setHeader("X-Powered-By", "Grain");
  }
}
```

`@Order` sets the priority of the middleware. Lower numbers are called before
higher. This can be used to configure the middleware chain.