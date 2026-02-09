# memory_bombs

A Java Swing-based memory matching game with FlatLaf UI.

## Building and Running

This project uses Gradle as its build system.

### Prerequisites

- Java 21 or higher
- The Gradle wrapper is included, so you don't need to install Gradle separately

### Building the Project

To build the project, run:

```bash
./gradlew build
```

On Windows, use:

```cmd
gradlew.bat build
```

This will:
- Compile the source code
- Run tests
- Create JAR files in `build/libs/`

### Running the Application

To run the application directly:

```bash
./gradlew run
```

Or run the packaged JAR:

```bash
java -jar build/libs/memory_bombs-1.0-SNAPSHOT-launcher.jar
```

### Running Tests

To run the test suite:

```bash
./gradlew test
```

View test reports at: `build/reports/tests/test/index.html`

### Common Gradle Tasks

- `./gradlew clean` - Cleans build outputs
- `./gradlew build` - Builds the project
- `./gradlew test` - Runs tests
- `./gradlew run` - Runs the application
- `./gradlew shadowJar` - Creates a fat JAR with all dependencies
- `./gradlew tasks` - Lists all available tasks

## Adding Dependencies

This project uses Gradle with Kotlin DSL for dependency management. To add dependencies, edit the `build.gradle.kts` file.

### Adding a Runtime Dependency

To add a library that your application needs at runtime, add it to the `dependencies` block:

```kotlin
dependencies {
    implementation("group:artifact:version")
}
```

**Example:** Adding Apache Commons Lang

```kotlin
dependencies {
    implementation("org.apache.commons:commons-lang3:3.14.0")
    // ... existing dependencies
}
```

### Adding a Test Dependency

For test-only dependencies:

```kotlin
dependencies {
    testImplementation("junit:junit:5.10.0")
}
```

### Adding Annotation Processors

For annotation processors like Lombok:

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}
```

### Finding Dependencies

You can search for dependencies on:
- [Maven Central](https://search.maven.org/)
- [MVN Repository](https://mvnrepository.com/)

Most dependencies use the format: `"groupId:artifactId:version"`

### Dependency Scopes

Common dependency configurations:
- `implementation` - Compile and runtime dependency (preferred)
- `api` - Compile and runtime dependency (exposes to consumers)
- `compileOnly` - Compile-time only (not included in runtime)
- `runtimeOnly` - Runtime only (not available at compile-time)
- `testImplementation` - Test compile and runtime
- `testCompileOnly` - Test compile-time only
- `annotationProcessor` - Annotation processors

### Using a Bill of Materials (BOM)

To manage versions across multiple related dependencies:

```kotlin
dependencies {
    implementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")  // version from BOM
    testImplementation("org.junit.jupiter:junit-jupiter-params")  // version from BOM
}
```

### Adding Repositories

By default, this project uses Maven Central. To add other repositories, edit the `repositories` block in `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://repository-url.com/maven")
    }
}
```

### Refreshing Dependencies

After adding dependencies, Gradle will automatically download them on the next build. To force a refresh:

```bash
./gradlew build --refresh-dependencies
```

## Project Structure

```
├── build.gradle.kts          # Gradle build configuration (Kotlin DSL)
├── settings.gradle.kts       # Gradle settings
├── gradle.properties         # Project properties
├── gradlew                   # Gradle wrapper script (Unix)
├── gradlew.bat              # Gradle wrapper script (Windows)
├── src/
│   ├── main/
│   │   ├── java/            # Java source files
│   │   └── resources/       # Resource files
│   └── test/
│       └── java/            # Test source files
└── build/                   # Build outputs (generated)
```

## License

MIT License - See the license in pom.xml for details.
