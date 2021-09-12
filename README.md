# Grain

## Description

Zero dependency web framework based on Spring.

This is a pet project to showcase programming paradigms, stereotypes and patterns.

## Functionality

* Java based configuration
* HTTP Parsing
  * Response codes
  * JSON and text body
  * HTTP Headers
* Multi-threaded request handling
* Component system
  * Dependency injection (constructor parameters and annotated fields)
  * Middleware
  * Controllers
    * Method request mapping
    * View templates
      * Extremely basic interpreted template language
* Static directory serving
  * Classpath and non-classpath directories
* Custom generated error pages

## Examples

### Basic Main class with configuration

```java
@GrainApplication
public class Main extends GrainApp {
  @Override
  protected void configure(ConfigurationBuilder builder) {
    builder.port(80);
  }

  public static void main(String[] args) {
    GrainAppRunner.run(Main.class);
  }
}
```

### Controller examples

```java
@Controller
@RequestMapping("/")
public class TestController {
  // field dependency injection
  @Inject
  public TestService testService;

  // returning json response
  @RequestMapping(value = "/json", method = HttpMethod.GET)
  public JsonResponse<Data> getUser() {
    return JsonResponse.ok(testService.getData());
  }

  // injected HTTP request and response objects
  @RequestMapping("/headers")
  public String getHeaders(HttpResponse response, HttpRequest request) {
    System.out.println(request.getHeader("Host"));
    response.setHeader("Test-Header", request.getHeader("Test-Header"));
    return "<body>Header example</body>";
  }

  // returning templates
  @RequestMapping("/index")
  public View getIndex() {
    return new View("index.html");
  }

  // parsing data inside of templates
  @RequestMapping("/data-view")
  public DataView getDataView(HttpRequest request) {
    DataView view = new DataView("index.html");
    view.setData("username", request.getStringParameter("username"));
    return view;
  }

  // parsing json body
  @RequestMapping(value = "/user", method = HttpMethod.POST)
  public String postUser(@JsonBody User user) {
    return user.username;
  }
}
```

### Template examples

```html
<!doctype html>
<html lang="en">
<body>
<nav>
  <% if username == null then %>
  <a href="/login">Login</a>
  <% else %>
  <a href="/logout">Logout</a>
  <% endif %>
</nav>

<% if username == "tom" then %>
<h1>Hello <%=username%></h1>
<% else %>
<h1>Welcome</h1>
<% endif %>

</body>
</html>
```

### Middleware examples

```java
@Grain
@Priority(1)
public class AuthMiddleware implements Middleware {

  @Override
  public boolean handle(HttpRequest httpRequest, HttpResponse httpResponse) throws HttpException {
    if (!Objects.equals(httpRequest.getHeader("Authorization"), "true")) {
    	throw new HttpException.Unauthorized(httpRequest.getPath());
    }
    return false;
  }
}
```

### Service examples

```java
@Grain
public class TestServiceImpl implements TestService {
  private OtherService otherService;	
  
  // constructor dependency injection
  public TestServiceImpl(OtherService otherService) {
	  this.otherService = otherService;
  }
	
  public Data getData() {
	  return new Data();
  }
}
```