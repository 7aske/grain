# Database

Currently, database connection is only possible with and tested for
MariaDB/MySQL but should, in theory, work with other SQL databases. Before
creating models database parameters should be configured.

## Configuration

Database parameter configuration can be done through the built-in Configuration
class. All database fields are required as none of them are included in the
default configuration.

```java
public class CinemaApp extends GrainApp {

  @Override
  protected void configure(ConfigurationBuilder builder) {
    builder.setProperty(DATABASE_USER, "grain");
    builder.setProperty(DATABASE_PASS, "grain");
    builder.setProperty(DATABASE_NAME, "cinema");
    builder.setProperty(DATABASE_HOST, "127.0.0.1");
    builder.setProperty(DATABASE_PORT, 3306);
    builder.setProperty(DATABASE_DRIVER_CLASS, "com.mysql.jdbc.Driver");
  }

  public static void main(String[] args){
    GrainAppRunner.run(CinemaApp.class);
  }
}
```

## Models

`Model` is the base class that implements all the logic of communicating with
the database. User-defined model classes must inherit the `Model` class.

```java
@Table(name = "movie")
public class Movie extends Model {
  @Id
  @Column(name = "movie_id")
  private Long id;
  @Column(name = "image_url")
  private String url;
  @Column(name = "title")
  private String title;
  @Column(name = "description")
  private String description;
  @Column(name = "genre")
  private String genre;
  @Column(name = "duration")
  private Integer duration;
  @Column(name = "director")
  private String director;
  @Column(name = "release_date")
  private LocalDate releaseDate;
  @OneToMany(column = "movie_id", referencedColumn = "movie_fk", table = "screening")
  private List<Screening> screenings;
}

@Table(name = "screening")
public class Screening extends Model {
  @Id
  @Column(name = "screening_id")
  private Long id;
  @Column(name = "time")
  private LocalDateTime time;
  @ManyToOne(column = "room_fk", referencedColumn = "room_id", table = "room")
  private Room room;
}
```

* `@Table` - annotation that binds the class to a table in the database.
  * `name` - name of the table in the database.

* `@Column` - specifies the column name that the attribute references.
  * `name` - name of the column in the database.

* `@Id` - specifies that the `@Column` annotated attribute is representing a
  primary key.
  * `autoIncrement` - specifies whether the column value is generated by the
    database.

* `@OneToMany` - specifies that the List attribute represents a one-to-many
  relationship.
  * `column` - name of the column in the current table which has the id if the
    current table.
  * `referencedColumn` - column in the joined table which has the foreign key of
    current table.
  * `table` - name of the joined table.

* `@ManyToOne` - specifies that the Class attribute represents a many-to-one
  relationship.
  * `column` - name of the column in the current table which has the foreign key
  * `referencedColumn` - column in the joined table that is used for joining.
    Typically, a primary key.
  * `table` - name of the joined table.

With these basic annotations user can declare a model class that accurately maps
to a table in the database.

### Model operations

Every `Model` subclass has access to CRUD operations through `Model` class
inheritance. Unfortunately, currently, the user is required to pass the
reference of the model class to `find` methods. This may or may not change in
the future.

`Model` class contains basic `find` operations.

* findAll
* findAllBy
* findById

All the mentioned methods require an instance of the class to be passed as the first parameter.

```java
@Grain
public class ScreeningServiceImpl implements ScreeningService {
  @Override
  public int getRemainingSeats(Integer screeningId) {
    Screening screening = Screening.findById(Screening.class, screeningId);
    Room room = screening.getRoom();
    List<Reservation> reservations = Reservation.findAllBy(Reservation.class,
                "screening_fk", screening.getId());
    return room.getSeats() - reservations.size();
  }
}
```

`findAllBy` and other `findBy` method variants filter results by their real
database column names.

Other methods `delete`, `save`, `update` can bre called directly on the model
object in question. Here's an example of CRUD service:

```java
@Grain
public class MovieServiceImpl implements MovieService {
  @Override
  public List<Movie> findAll() {
    return Movie.findAll(Movie.class);
  }
  
  @Override
  public Movie findById(Integer movieId) {
    return Movie.findById(Movie.class, movieId);
  }
  
  @Override
  public Movie updateMovie(Movie movie) {
    return movie.save();
  }
  
  @Override
  public Movie updateMovie(Movie movie) {
    return movie.update();
  }

  @Override
  public void deleteMovie(Movie movie) {
    movie.delete();
  }
  
  @Override
  public void deleteMovieById(Integer movieId) {
    Movie movie = Movie.findById(Movie.class, movieId);
    movie.delete();
  }
}
```
### Composite keys

Note that the support for composite primary keys is experimental at this moment.
To achieve a composite key model configuration just annotate multiple `@Column`
annotated fields with `@Id` annotation and set the autoIncrement property to
false.

## Connection

Database connection is preformed through the `ConnectionManager`
Grain. `ConnectionManager` has a configurable reusable connection pool which
keeps open connections to lower the latency of services that are communicating
with the database. Injectable Grain that the user can use to execute arbitrary
database queries is the `ConnectionPool` Grain. Number of concurrent connections
in the pool can be configured with the `DATABASE_POOL_SIZE`
configuration property. By calling `ConnectionPool#getConnection` user receives
an instance of the `ConnectionWrapper` class which he can use to preform
queries. For now this is not a generic way to connect to the database as it only
uses the JDBC type driver classes for communicating with MySQL/MariaDB or
similar SQL database servers.
