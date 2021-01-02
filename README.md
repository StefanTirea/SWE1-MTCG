# Monster Trading Card Game

## About the Game

This HTTP/REST-based server is built to be a platform for trading and battling with
and against each other in a magical card-game world.
* register (unique username, password)
* login and authorize yourself with a Bearer Token
* a user can:
  * edit username & password
  * show user profile (name, inventory, coins, elo, ...)
  * show user stats and the result of the recent battles
  * buy a random generated card package (5 coins)
  * open the package
  * create / list / send / delete a trade offer
  * configure a deck of 4 cards
  * search for a battle. If no player was found after 10 seconds, the search is stopped
* look at the elo rank list of all registered users

## Technical Architecture

The project consists of two scopes:
* a HTTP REST Server built from scratch using Sockets
* the MTCG with all the models, business code for the cards/game, persistence with PostgreSQL & REST endpoints

### HTTP Server

The server is programmed from scratch using `Sockets` and threading.
Each request is handled in a new `Thread` in the `RequestWorker` class. There the request is being
parsed and validated. All exceptions which may come up are handled here & the user gets an error response
with the corresponding HTTP status code. <br><br>

Before the server is even started, a lot of background stuff happens to ease the development (based on Spring Boot) of the REST endpoints:
* **Automatically find REST endpoints**: Using Annotations & Reflections, the `RequestHandler` automatically knows the paths and which parameters are needed
to process the request. These are generated in `ControllerFinder`.
  * Use `@Controller` to annotate the endpoint class
  * Annotate the methods with: `@Get("/api/test/{anyName}")`, `@Post`, `@Put`,`@Delete`
  * Annotate the methods parameter with:
    * `@PathVariable`: if you want to access a variable in the path
    * `@RequestBody`: if you want to access the object in the body
  * Both ways support automatic type conversion!
  * Parameters of type `Authentication` or `HttpExhange` are also automatically injected


* **Dependency Injection**: Furthermore, a simple form of constructor based **Dependency Injection** is used with `@Component`<br>
Example:
  ```java
  @Component // This class is now also a candidate to be injected
  @RequiredArgsConstructor
  public class Demonstration {
  
      private final TestRepository testRepository; // automatically injected
  }
  ```
  The `ComponentFinder` looks for all Component classes and automatically initializes the constructor with other Component classes!
  If this is not possible because one of the classes is not a Component class, the server does not start and throws a error
  message with some clues where to look.


* **Filter Chains**: The `FilterFinder` looks for classes which implement the interface `PreFilter` or `PostFilter`.
  These are executed before/after the call to the endpoint method. The `FilterManager` invokes the actual endpoint and the filters. <br>
  This is also how the Bearer Token **Authentication** is implemented, located in the mtcg package.


* **Controller Security**: The `@Secured` can be used on a Controller class and/or the Endpoint Method.
  If used, the request **must** contain a user with any role. If a specific role is required,
  it can be added in the annotation `@Secured("ADMIN")` or `@Secured({"ADMIN", "USER"})`. Can be combined
  with the Authentication Object injection, to insure it is not null.
  
#### Pitfalls

My Pitfalls designing & programming the HTTP Server are documented
[here](https://github.com/StefanTirea/SWE1-MTCG/wiki/Http-Server).

### MTCG + Endpoints



#### Pitfalls

My Pitfalls designing & programming the HTTP Server are documented
[here](https://github.com/StefanTirea/SWE1-MTCG/wiki/MTCG-Endpoints-&-Persistence).

## Getting Started

Gradle is used to build the Project.
Use `java -jar mtcg.jar` to run the HTTP Server.

## Artifacts

See Github Packages or Releases.

## Maintainer

* Stefan Tirea BIF3C
