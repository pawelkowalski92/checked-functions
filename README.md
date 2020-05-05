# Checked Functions

Java 8 has provided a lot of convenient features that support functional programming: Stream API, functional interfaces, method references, etc.

Unfortunately, the signatures of abstract methods in the most of these interfaces do not include **checked** exceptions, which limits their usability;
in other words instead of this:

```jshelllanguage
Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)            
        .flatMap(Files::lines)
        .forEach(System.out::println);
```

we need to do resort to:

```jshelllanguage
Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)
        .flatMap(p -> {
            try {
                return Files.lines(p);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }).forEach(System.out::println);
```

Of course - it's possible to extract problematic part to separate method and provide another reference.
On the other hand, this module enables you to shorten problematic code by providing set of corresponding interfaces that cover checked exceptions
and are easily convertible:

```jshelllanguage
Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)
        .flatMap(CheckedFunction.wrap(Files::lines))
        .forEach(System.out::println);
```

## Getting started

### Installation

This library is not yet available in Maven central repository, so you need to add it to local repository manually.

- clone it into a local repository  
  ```shell script
  $ git clone https://github.com/pawelkowalski92/checked-functions.git
  $ cd checked-functions/
  ```
- build the library and install it in your local repository using **Gradle** (use either standalone version or supplied wrapper)  
  ```shell script
  $ ./gradlew build publishToMavenLocal
  ```
- once installed, reference this library in your project

    - Maven
    ```xml
    <dependency>
        <groupId>com.github.pawelkow</groupId>
        <artifactId>checked-functions</artifactId>
        <version>${version}</version>
    </dependency>
    ```
  
    - Gradle
    ```kotlin
    dependencies {
        api("com.github.pawelkow:checked-functions:${version}")
    }
    ```

### Usage

Select appropriate interface (this library contains extensions for all *functional interfaces* present in package `java.util.function`) and use static function
`wrap` to apply it in your context.

- simple customizations

    once selected, you may customize function behavior in case of exception, e.g.

    ```jshelllanguage
    //other imports
    import static com.github.pawelkow.function.CheckedFunction.wrap;
  
    Stream.of("/logs", "/archives")
            .map(Paths::get)
            .flatMap(wrap(Files::list).supplyFallback(Stream::empty))
            .flatMap(wrap(Files::lines))
            .forEach(System.out::println);
    ```
  
- more complex customizations

    instead of shorthand methods available to each `checked` interface, you may build your own `ExceptionResolver` to provide more complex behavior:
    
    ```jshelllanguage
    //other imports
    import static com.github.pawelkow.function.CheckedFunction.wrap;
    import com.github.pawelkow.function.handler.ReferenceHandler;
  
    Stream.of("/logs", "/archives")
            .map(Paths::get)
            .flatMap(wrap(Files::list).handleException(new ReferenceHandler<Stream<Path>>()
                    .inCaseOf(NoSuchFileException.class, NotDirectoryException.class).handle(System.err::println).discard()
                    .inCaseOf(IOException.class).rethrow(UncheckedIOException::new)
                    .inCaseOf(SecurityException.class).supplyValue(Stream::empty)
                    .inAnyCase().discard()))
            .flatMap(wrap(Files::lines))
            .forEach(System.out::println);
    ```
    
    The order of resolvers registered in `ReferenceHandler` **does** matter; first one matching the exception type will handle the resolution
    (similar to *try-catch* block where most detailed exception type should be listed in the first block, but here compiler doesn't remind us about it ;) )

### Known limitations

Utility methods used for interface wrapping seem to have trouble with *method references* for overloaded methods in complex customization scenarios - compiler
cannot infer proper type parameters for provided interface e.g.

```jshelllanguage
//other imports
import static com.github.pawelkow.function.CheckedFunction.wrap;
import com.github.pawelkow.function.handler.ReferenceHandler;

Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)
        .flatMap(wrap(Files::lines).handleException(new ReferenceHandler<Stream<String>>() //cannot resolve method *lines*
               .inCaseOf(NoSuchFileException.class).handle(System.err::println).discard()
               .inCaseOf(IOException.class).mapToValue(ex -> Stream.of("ERROR: " + ex.getMessage()))))
        .forEach(System.out::println);
```

This may be easily fixed by either adding type parameters:

```jshelllanguage
import com.github.pawelkow.function.CheckedFunction;
import com.github.pawelkow.function.handler.ReferenceHandler;

Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)
        .flatMap(CheckedFunction.<Path, Stream<String>, IOException>wrap(Files::lines) //OK
                .handleException(new ReferenceHandler<Stream<String>>()
                        .inCaseOf(NoSuchFileException.class).handle(System.err::println).discard()
                        .inCaseOf(IOException.class).mapToValue(ex -> Stream.of("ERROR: " + ex.getMessage()))
                ))
        .forEach(System.out::println);
```

or using a *lambda expression* instead:

```jshelllanguage
//other imports
import static com.github.pawelkow.function.CheckedFunction.wrap;
import com.github.pawelkow.function.handler.ReferenceHandler;

Stream.of("fileA.txt", "fileB.txt")
        .map(Paths::get)
        .flatMap(wrap((Path p) -> Files.lines(p)).handleException(new ReferenceHandler<Stream<String>>() //OK
                .inCaseOf(NoSuchFileException.class).handle(System.err::println).discard()
                .inCaseOf(IOException.class).mapToValue(ex -> Stream.of("ERROR: " + ex.getMessage()))))
        .forEach(System.out::println);
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)