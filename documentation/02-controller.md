# Controller

Controllers are one of the core concepts of the framework and the dependency
injection. Controllers are classes responsible for receiving and handling HTTP
requests. They are basically the entry point of the application from the context
of the client.

Here we have an example for an endpoint returning the information of available
seats for our imaginary Cinema app.

## Code example

```java
@Controller
@RequestMapping("/screenings")
public class ScreeningController {
  @Inject
  private ScreeningService screeningService;

  @GetMapping
  public JsonResponse<List<Screening>> getScreenings(@RequestParam("page") Pageable page) {
    return JsonResponse.ok(Screening.findAll(Screening.class, page));
  }

  @GetMapping("/{id}")
  public View getScreeningById(@PathVariable("id") Integer id) {
    TemplateView templateView = new TemplateView("screening.html");
    templateView.setData("screening", screeningService.findById(Screening.class, id));
    return templateView;
  }

  @GetMapping("/{id}/seats")
  public String getAvailableSeats(@PathVariable("id") Integer id) {
    return String.valueOf(screeningService.getRemainingSeats(id));
  }
}
```

* `@Controller` - specifies that the class is a controller and marks it for
  participation in dependency injection system.

* `@RequestMapping("/screenings")` - specifies that all request matching this
  pattern (Ant matching) will be handled by this controller.

* `@GetMapping("/{id}/seats")` - alias for `@RequestMapping(value = "/{id}/seats", method = HttpMethod.GET)`.
  Same as controller annotation - specifies that the method will be responsible for
  handling requests matching combination of controller request mapping path and its path.
  In this case it would be `/screenings/{id}/seats`. Other similar annotations
  are `@PostMapping`, `@PutMapping` and others for each of the HTTP spec methods.

* `@PathVariable("id")` - marks the method parameter for binding with request path.
  String value `"12"` from, for example `/screenings/12/seats`, the path will be parsed
  according to the type of the method parameter - in this case Integer. Classes used
  as path variables can be anything as long as a valid [Converter](./03-converter.md) class
  has been registered in the converter registry.

* `@RequestParam("page")` - marks the method parameter for binding with a request parameter.
For example in this case this would be parsed from the `page` parameter `/screenings?page=0`.
RequestParam requires for the appropriate Converter to be registered in order to
convert the request param string to the type of the handler argument.
 
## Handler method

### Request mapping

Controller handler methods are responsible for handling HTTP requests. Method marked with
any of the Request Mapping annotations becomes a handler method. Request Mapping
annotations are one of the following:

* `@RequestMapping(value = "", method = HttpMethod.*)`
* `@GetMapping(value = "")`
* `@PostMapping(value = "")`
* `@PutMapping(value = "")`
* `@DeleteMapping(value = "")`
* `@PatchMapping(value = "")`
* `@HeadMapping(value = "")`
* `@TraceMapping(value = "")`
* `@OptionsMapping(value = "")`

### Return types

There is a variety of return types supported by the framework for the handler methods.
Depending on the return type framework will do additional (or no additional) processing
of the value before returning it to the client.

Supported return types are:

* `View` - handlers returning a `View` class will to the client return the result of
View#getContent method with appropriate content type parsed by the View.
 
* `TemplateView` - subclass of View interface with additional GTL (Grain Template Language)
processing. `ControllerHandler` will pass some implicit objects to the view such as:
`HttpRequest` as `request`, `HttpResponse` as `response`, `Session` as `session`
and `Authentication` as `authentication`.
 
* `JsonResponse`, `JsonObject` - will return a JSON type response with appropriate content type.

* `Object[]` - any object array will be parsed as a JSON array response.
 
* `String` - will just return the contents of the string as a `text/plain` response
Special case: if the string response ends with `redirect:` will send a 302 redirect
to the client with the remainder after the `redirect:` prefix as the value of 
`Location` header.

* Anything else - will be returned by calling Object#toString method and as a 
`text/plain` response.