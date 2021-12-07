# Converter

Converter is a class responsible for converting path and request parameters
to instances other classes.

## Code example

Here we have an example of a converter that converts a string to an instance of
a Pageable interface. Every Converter will perform its conversion in a 
`T convert(String)` and return a nullable instance of the class defined as the
generic type.

```java
/**
 * Describes how request parameter strings are converted to objects.
 * @param <T> Type of the object that the parameters is converted to.
 */
public interface Converter<T> {
  T convert(String param);
}
```

```java
public class PageableConverter implements Converter<Pageable> {
  public static final int DEFAULT_PAGE_SIZE = 10;

  @Override
  public Pageable convert(String queryString) {
    if (queryString == null || queryString.isEmpty())
      return null;

    String[] attrs = queryString.split(",");

    return new DefaultPageable(
      parsePageNumber(attrs),
      parsePageSize(attrs)
    );
  }

  private Integer parsePageNumber(String[] attrs) {
    // ...
  }

  private Integer parsePageSize(String[] attrs) {
    // ...
  }
}
```

# Converter registry

Converter is a Grain responsible for keeping track of all viable converters. This is 
done so that finding a converter is as easy as querying a Map with a class of a wanted
Converter instead of searching all registered Grains for the right instance of a converter.

## Code example

```java
/**
 * Here we register all the converters we might need.
 */
@Grain final class ConverterRegistryConfigurerRunner {
  @Inject
  private ConverterRegistry converterRegistry;

  @AfterInit
  public void setup() {
    converterRegistry.registerConverter(Pageable.class, new PageableConverter());
    converterRegistry.registerConverter(Integer.class, Integer::parseInt);
    converterRegistry.registerConverter(Float.class, Float::parseFloat);
    converterRegistry.registerConverter(Long.class, Long::parseLong);
    converterRegistry.registerConverter(Boolean.class, Boolean::parseBoolean);
    converterRegistry.registerConverter(Short.class, Short::parseShort);
    converterRegistry.registerConverter(Byte.class, Byte::parseByte);
  }
}
```

This is the default Configurer of the ConverterRegistry. Note that you should not 
override the ConverterRegistry Grain or you will lose the ability to convert
to these specified default types. We register a Converter by calling
`ConverterRegistry#registerConverter(Class<T>, Converter<T>)` with the appropriate
values.