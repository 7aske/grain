# View

View is a basic interface for returning files (mainly HTML templates) from Request Handler methods.
Files are by default loaded from the disk using the ContextClassLoader which means
the relative path starts in the project `resources` folder.

## Basic View interface

```java
/**
 * Basic interface describing the semantics of a View response
 */
public interface View {
  /**
   * @return String that is going to be written to the HttpResponse.
   */
  String getContent();

  /**
   * @return content type of the response. Should be by default text/plain
   * but the user is free to set any other value.
   */
  String getContentType();
}
```

## FileView implementation

This is an implementation of a FileView - class that reads a file from the disk
and returns it as a response. Additional processing can be applied with this method
unlike with the method of serving static files with the StaticRequestHandler.

```java
public class FileView implements View {
  // fields ...
    
  FileView(String path) {
    this.path = path;
    this.contentType = probeContentTypeNoThrow(path,
            /*default*/ "text/html");
  }

  public String getContent() {
    // returns the contents of the file ...	  
  }

  public String getContentType() {
    return this.contentType;
  }
}
```

### Code example

Using FileView in a controller is rather simple:

```java
@Controller
@RequestMapping
public class AuthController {
  @GetMapping("/login")
  public View getLogin() {
    // resources/login.html
    return new FileView("login.html");
  }
}
```

## TemplateView implementation

TemplateView is used preform complex templating using [GTL](#TODO) (Grain Template Language).
GTL is evaluated in the HTML page context with some implicit objects available
to the user. Template is loaded in the same fashion as it is in the FileView, but it
is parsed and GTL scriptlets are evaluated to produce dynamic content. Additional data
can be passed to the template using TemplateView#setData method, and it can be accessed from the template as global
variables.

```java
public class TemplateView extends FileView {
  // fields ...
  public TemplateView(String path) {
    super(path);
  }

  public void setData(String key, Object value) {
    // add data for GTL interpretation
  }

  @Override
  public String getContent() {
    // preform GTL interpretation
    // return parsed HTML  
  }
}
```

### Code example

Here we can see an example of passing the fetched `screening` object to the template.

```java
@Controller
@RequestMapping("/screenings")
public class ScreeningController {
  @Inject
  private ScreeningService screeningService;
  
  @GetMapping("/{id}")
  public View getScreeningById(@PathVariable("id") Integer id) {
    TemplateView templateView = new TemplateView("screening.html");
    templateView.setData("screening", screeningService.findById(Screening.class, id));
    return templateView;
  }
}
```
