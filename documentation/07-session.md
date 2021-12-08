# Session

There are 4 interfaces that are used to describe the process of tracking users
session in the framework:

* `SessionInitializer`
* `HttpRequestSessionTokenProviderStrategy`
* `SessionStore`
* `Session`
* `SessionToken`

Session can be configured with `SESSION_ENABLED` and `SESSION_MAX_AGE`
configuration keys.

## Session Initializer

Describes how the session is initialized from the request and how the session
token is extracted using `HttpRequestSessionTokenProviderStrategy`. In the
default case Grain `CookieSessionInitializer` is the default implementation, and
it describes that the Session is initialized from the request by using Http
Cookies. For example this implementation can be replaced with JWT as the session
token if the appropriate JWT implementation implements the `SessionToken`
implementation.

## Http Request Session Token Provider Strategy

Describes how the session token is extracted from the incoming `HttpRequest`. By
default, the cookie is extracted from the request, and it is a
valid `SessionToken` implementation.

## Session Store

Describes how the session and its data is stored. By default, it is
a `InMemorySessionStore` which saves all the data in the memory of the program.
For example this implementation can be overridden to save the session data to
the database or some other storage medium.

## Session

Describes all the functionality the session should have and which methods are
available to manipulate its data. Default session implementation is
the `SessionStoreDelegate` which basically just delegates method calls to
the `SessionStore`.

## Session Token

Describes the unique session identification used to track the session. By
default, it is the Cookie but can be easily replaced
with `HttpRequestSessionTokenProviderStrategy` to track the session with, for
example, a JWT.

## Session injection

Session object cannot be injected using the dependency injection system. It is
rather passed by the request handler runner to its child runners where it can be
accessed in the for example controllers or the TemplateView.

```java
@Grain
public class RequestCountMiddleware implements Middleware {
  @Override
  public boolean handle(HttpRequest req, HttpResponse res, Session session) {
    session.put("request-count", ((int) session.get("request-count")) + 1);
    return false;
  }
}
```
