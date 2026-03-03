# Rate Limiter Service

A resilient API Gateway and Rate Limiter Service built with Spring Boot, Spring Cloud Gateway, and Redis. It is designed to proxy requests while providing robust rate-limiting, security, and observability features.

## Features

- **API Gateway**: Proxies and routes requests using Spring Cloud Gateway (Reactive).
- **Rate Limiting**: Uses Reactive Redis to enforce rate limits on incoming requests.
- **Resilience**: Integrated with Resilience4j for circuit breaking and fault tolerance.
- **Security**: Secured endpoints out-of-the-box with Spring Security.
- **Observability**: Prometheus metrics via Micrometer and Spring Boot Actuator.
- **API Documentation**: Automated OpenAPI documentation via Springdoc.

## Tech Stack

- Java 21
- Spring Boot 3.2.x
- Spring Cloud 2023.0.x
- Redis (Data Store for Rate Limiting)
- Maven
- Docker

## Prerequisites

- **Java 21**
- **Maven** (or use the provided `mvnw` wrapper)
- **Redis** server running (default localhost:6379, depending on configuration)
- **Docker** (optional, for running via containers)

## Getting Started

### 1. Build the Application

Use Maven to build the executable JAR:

```bash
./mvnw clean package
```

*(Note: tests can be skipped with `-DskipTests` if needed)*

### 2. Run the Application locally

Run the application using the Spring Boot Maven plugin or the generated JAR:

```bash
./mvnw spring-boot:run
```

Or run the jar file directly:

```bash
java -jar target/rate-limiter-service-0.0.1-SNAPSHOT.jar
```

The application will start on port `8080` by default.

### 3. Run with Docker

A `Dockerfile` is provided for containerized deployment.

Build the Docker image:

```bash
docker build -t rate-limiter-service .
```

Run the Docker container:

```bash
docker run -p 8080:8080 rate-limiter-service
```

## Useful Endpoints

Once the service is running, you can access the following endpoints:

- **Actuator Health**: `http://localhost:8080/actuator/health`
- **Prometheus Metrics**: `http://localhost:8080/actuator/prometheus` (if enabled in configuration)
- **OpenAPI Swagger UI**: `http://localhost:8080/swagger-ui.html`

## Configuration

Standard application properties can be found and configured in `src/main/resources/application.properties` or `application.yml`.
Ensure the Redis connection details are correctly configured for the rate limiting to function properly.

## License & Authors

Generated via Spring Initializr.
