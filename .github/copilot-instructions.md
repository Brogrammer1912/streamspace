# StreamSpace - GitHub Copilot Instructions

## Project Overview

StreamSpace is a Java-based media streaming and torrent management application built with Spring Boot. It provides functionality for:
- Searching and streaming movies via YTS API and other sources
- Managing torrent downloads using the Bt library
- Playing local media files (videos and music)
- Managing watchlists and user preferences
- Real-time download progress via WebSocket/SSE

## Technology Stack

- **Java Version**: JDK 25 (configured for virtual threads)
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Maven 3.9.6
- **Database**: H2 (file-based at `~/.h2`)
- **ORM**: Spring Data JPA / Hibernate
- **Template Engine**: Thymeleaf with HTMX
- **Torrent Engine**: Bt library (v1.10)
- **Frontend**: Bootstrap 5.3.8, HTMX 2.0.8, Bootstrap Icons
- **Testing**: Spring Boot Test

## Project Structure

```
src/main/java/com/brogrammer/streamspace/
├── StreamSpaceApplication.java          # Main application entry point
├── common/                              # Common constants and enums
├── config/                              # Spring configuration classes
├── content/                             # Media content management (videos, music)
├── downloads/                           # Download task management
├── preferences/                         # User preferences
├── resilience/                          # Retry logic and resilience patterns
├── services/                            # Background services and scheduled jobs
├── torrentengine/                       # Torrent client and download management
├── watchlist/                           # Watchlist functionality
├── www/                                 # Web controllers and external API clients
└── yt/                                  # YouTube integration
```

## Build and Run

### Build the project:
```bash
mvn clean install
```

### Run the application:
```bash
cd target
java -jar streamspace-0.0.1.jar
```

### Access the application:
- Web UI: https://localhost:8080/
- H2 Console: Configure via application.properties if needed

## Coding Conventions

### General Java Style

1. **Use Lombok annotations** to reduce boilerplate:
   - `@Slf4j` for logging
   - `@RequiredArgsConstructor` for constructor injection
   - `@Getter`, `@Setter`, `@NoArgsConstructor` for entities
   - Avoid `@Data` to maintain explicit control

2. **Dependency Injection**:
   - Use constructor injection with `final` fields
   - Prefer `@RequiredArgsConstructor` over explicit constructors
   - Example:
     ```java
     @Controller
     @RequiredArgsConstructor
     public class WebController {
         final UserPreferences userPreferences;
         final WatchList watchList;
     }
     ```

3. **Package Organization**:
   - Group by feature/domain (not by layer)
   - Keep related entities, repositories, and controllers together
   - Use package-private visibility when appropriate

4. **Naming Conventions**:
   - Controllers: `*Controller` (e.g., `WebController`, `MusicController`)
   - Services: `*Service` or descriptive names (e.g., `BackgroundServices`)
   - Repositories: `*Repository` (Spring Data JPA interfaces)
   - Entities: Simple nouns (e.g., `Watch`, `Video`, `Song`)
   - DTOs: `*DTO` suffix (e.g., `YouTubeResponseDTO`)

### Spring Boot Specific

1. **Controllers**:
   - Use `@Controller` for web pages (Thymeleaf)
   - Use `@RestController` for REST APIs
   - Keep controllers thin - delegate to services
   - Use `Model` to pass data to views

2. **Entities**:
   - Use JPA annotations (`@Entity`, `@Id`, `@GeneratedValue`)
   - Use `GenerationType.IDENTITY` for auto-generated IDs
   - Add `@CreatedDate` for audit fields

3. **Configuration**:
   - Use `@Configuration` classes for Spring beans
   - Enable features via `@Enable*` annotations on main class
   - Keep configuration in `application.properties`

4. **Async and Scheduling**:
   - The application uses `@EnableAsync` and `@EnableScheduling`
   - Use `@Async` for background operations
   - Use `@Scheduled` for periodic tasks

### Error Handling

- Use proper exception handling in controllers and services
- Log errors appropriately using `@Slf4j` logger
- Provide meaningful error messages to users

### Logging

- Use SLF4J via Lombok's `@Slf4j`
- Log important operations at INFO level
- Log detailed debugging info at DEBUG level
- Log errors with stack traces when appropriate
- Example: `log.info("Starting torrent download: {}", torrentUrl)`

### Comments and Documentation

- Avoid unnecessary comments - write self-documenting code
- Add JavaDoc for public APIs and complex logic
- Include license headers where appropriate (Apache 2.0)
- Document complex algorithms and business logic

## Testing

- The project has minimal test coverage currently (main test: `StreamSpaceApplicationTests`)
- When adding new features:
  - Add unit tests for business logic
  - Add integration tests for controllers
  - Follow Spring Boot testing conventions
  - Use `@SpringBootTest` for integration tests

## Important Notes

### Security and Legal

- This is an **educational project** - emphasize responsible use
- Do not encourage copyright infringement
- Be mindful of legal implications when working with torrents
- The application requires Cloudflare 1.1.1.1 VPN for certain operations

### Platform Considerations

- The application sets `java.net.preferIPv4Stack=true` on Windows
- Uses virtual threads (available since Java 21, project uses Java 25)
- Configured for HTTP/2

### External APIs

- **YTS API**: For movie search and metadata
- **YouTube**: For trailer playback
- **Microsoft Store**: Additional content sources
- Be mindful of API rate limits and terms of service

## Configuration Files

### application.properties
- Server runs on port 8080 with HTTP/2
- H2 database at `~/.h2` (file-based persistence)
- JPA with `ddl-auto=update` for schema management
- Virtual threads enabled for improved concurrency

### pom.xml
- Java version: 25
- Spring Boot version: 3.5.7
- Key dependencies: Bt library, HTMX, Thymeleaf, Bootstrap WebJARs

## Common Tasks

### Adding a New Controller
1. Create in appropriate package (e.g., `www/` for web, `content/` for media)
2. Use `@Controller` or `@RestController`
3. Inject dependencies via constructor with `@RequiredArgsConstructor`
4. Follow existing URL mapping conventions

### Adding a New Entity
1. Create in the relevant feature package
2. Annotate with `@Entity`, Lombok annotations
3. Create corresponding repository interface extending `JpaRepository`
4. Add to appropriate service class

### Working with Torrents
- Use `TorrentClient` and `TorrentDownloadManager` classes
- Follow patterns in `torrentengine/` package
- Be aware of Bt library's threading model

## Improvement Areas

Refer to `docs/tasks.md` for a comprehensive list of improvement tasks, including:
- Architecture enhancements (layered architecture, better separation of concerns)
- Code quality improvements (removing duplication, better naming)
- Testing coverage
- Security hardening
- Performance optimization
- Documentation

## CI/CD

- GitHub Actions workflow at `.github/workflows/ci-build.yml`
- Builds with Java 25 and Maven 3.9.6 on Ubuntu
- Creates releases with JAR artifacts on push to main
- Run `mvn clean package` for local builds

## Additional Resources

- README.md: User-facing documentation and setup instructions
- docs/tasks.md: Prioritized improvement tasks
- pom.xml: Complete dependency list and build configuration
