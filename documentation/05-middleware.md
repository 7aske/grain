# Middleware

In Grain Middlewares are classes that preform additional logic for each HTTP request.
Middlewares return a boolean value. If the returned value is true Handler chain is
stopped and the response is returned to the user. Middlewares must be annotated
by the `@Grain` annotation otherwise it will not be picked up by the dependency 
injection system.

## Code example

```java
@Grain
public class PoweredByMiddleware implements Middleware {
  @Override
  public boolean handle(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
    httpResponse.setHeader("X-Powered-By", "Grain");
    return false;
  }
}
```