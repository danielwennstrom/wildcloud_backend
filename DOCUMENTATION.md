# WildCloud Backend

This README gives a compact, practical overview of the WildCloud backend so you and other developers can run, test and maintain the project.

---

## Quick summary
- Framework: Spring Boot (WebFlux / reactive)
- DB: PostgreSQL (R2DBC for runtime), Flyway for migrations
- Auth: JWT access tokens + refresh tokens (stateless). OAuth2 supported for external providers.
- Build: Maven

---

## Project structure (high level)
- `src/main/java/.../controller` - REST controllers and endpoint mappings
- `src/main/java/.../service` - business logic, validation and orchestration
- `src/main/java/.../repository` - reactive repositories (R2DBC)
- `src/main/resources/db/migration` - Flyway SQL migrations
- `src/main/resources/application.properties` - runtime configuration
- `src/main/java/.../security` - JWT utilities, authentication filter, security config

---

## Endpoints (exact routes in this project)
All endpoints are prefixed with `/api`.

Authentication (public):
- POST /api/AuthenticateUsers/createUser
  - Body: `UserRequestDTO` (e.g. `{ "userEmail": "me@example.com", "password": "pass123" }`)
  - Returns: created `UserDTO`
- POST /api/AuthenticateUsers/login
  - Body: `UserLoginDTO` (e.g. `{ "userEmail": "me@example.com", "password": "pass123" }`)
  - Returns: JSON with `user`, `accessToken`, `refreshToken`, `tokenType` and `id`.

User endpoints (require auth unless security is changed):
- POST /api/users/refreshToken
  - Body: `{ "refreshToken": "..." }` → returns new access token.
- POST /api/users/logout
  - Body: `{ "userEmail": "me@example.com" }` → deletes refresh token and logs out.
- GET /api/users/getAllUsers
  - Returns: list of `UserDTO`.
- PUT /api/users/updateUser/{userEmail}
  - Body: `UserRequestDTO` → update user by email.
- DELETE /api/users/deleteUser/{userId}
- GET /api/users/getUserById/{userId}

Camera endpoints:
- POST /api/cameras/createCamera
  - Body: `CameraRequestDTO` → create camera
- GET /api/cameras/getAllCameras
- GET /api/cameras/getCameraByCameraEmail/{cameraEmail}
- DELETE /api/cameras/deleteCameraByCameraEmail/{cameraEmail}
- PUT /api/cameras/updateCamera/{cameraEmail}

Notes:
- The controllers use DTOs (`UserDTO`, `UserRequestDTO`, `CameraDTO`, `CameraRequestDTO`). Passwords are never returned in DTO responses.

---

## Authentication details (JWT + refresh tokens)
- `JwtUtil` is used to generate and verify tokens. Tokens are signed with a symmetric secret (`jwt.secret`) and expire according to `jwt.expiration` (ms).
- Login flow (server-side):
  1. `POST /api/AuthenticateUsers/login` authenticates credentials.
  2. If credentials match, a JWT access token is created and a refresh token is generated & saved.
  3. Client stores the access token and uses it in an `Authorization: Bearer <token>` header for protected endpoints.
- Refresh flow:
  1. `POST /api/users/refreshToken` with a valid refresh token returns a new access token.
  2. Refresh tokens are persisted server-side and can be removed on `logout`.

Security implementation notes:
- Requests are checked by a reactive `AuthenticationFilter` that reads `Authorization` header and validates the token.
- Public endpoints include registration and login paths; everything else requires a valid JWT (depending on `SecurityConfig`).

---

## Environment & configuration
Keep secrets out of VCS. Prefer OS environment variables or a local `.env` imported into Spring properties.

Important properties (examples):
```properties
# R2DBC runtime
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/wildcloud
spring.r2dbc.username=postgres
spring.r2dbc.password=postgres_pw

# Flyway (uses JDBC driver)
spring.flyway.url=jdbc:postgresql://localhost:5432/wildcloud
spring.flyway.user=postgres
spring.flyway.password=postgres_pw
spring.flyway.locations=classpath:db/migration

# JWT
jwt.secret=very_long_random_secret_here
jwt.expiration=3600000

# Optional OAuth2 provider example
# spring.security.oauth2.client.registration.github.client-id=...
# spring.security.oauth2.client.registration.github.client-secret=...
```

Note: Flyway runs via JDBC at startup; ensure the PostgreSQL JDBC driver is on the classpath (in `pom.xml`) so migrations can run even though the app uses R2DBC at runtime.

---

## Quickstart (local development)
1. Start local PostgreSQL and create database `wildcloud` (or use your configured DB name).
2. Set environment variables (or `application.properties`) for DB credentials and `jwt.secret`.
3. Build & run the app:

Windows (cmd):

```cmd
mvn clean package
mvn spring-boot:run
```

or run from your IDE.

4. Flyway will run migrations on startup (check logs). If Flyway fails, inspect `flyway_schema_history` and SQL migrations in `src/main/resources/db/migration`.

---

## Testing the API (Postman / HTTP client)
Use Postman or an HTTP client (you said you prefer not to use curl — Postman / Insomnia / HTTPie are good alternatives).

Register a user (Postman):
- POST http://localhost:8080/api/AuthenticateUsers/createUser
- Body (JSON):
```json
{
  "userEmail": "alice@example.com",
  "password": "admin1",
  "firstName": "Alice",
  "lastName": "Example"
}
```

Login and collect tokens:
- POST http://localhost:8080/api/AuthenticateUsers/login
- Body (JSON):
```json
{
  "userEmail": "alice@example.com",
  "password": "admin1"
}
```
- Response example includes: `accessToken` and `refreshToken`.

Use token for protected request (Postman Header):
- Key: `Authorization`
- Value: `Bearer <accessToken>`

Refresh token:
- POST http://localhost:8080/api/users/refreshToken
- Body: `{ "refreshToken": "<refresh-token-from-login>" }`

Logout:
- POST http://localhost:8080/api/users/logout
- Body: `{ "userEmail": "alice@example.com" }` (this deletes refresh tokens and ends session logically)

---

## Working with cameras and relations (notes)
- `Camera` is a separate entity. To model many-to-many relationship between users and cameras in R2DBC, use a join table `user_camera` (entity `UserCamera`) and repositories to manage relations.
- Recommended pattern to list cameras for a user:
  1. Query `user_camera` rows by `user_id` (or `user_email`).
  2. For each relation row fetch `camera` by `camera_id` and map to `CameraDTO`.
  3. Return a `Flux<CameraDTO>` from the service and `collectList()` at controller level to produce a `List`.

Why use IDs (recommended):
- IDs (numeric PKs) are immutable and efficient for joins. Emails can change and storing them in relation rows can cause inconsistencies. You may still store email as a convenience column but rely on `user_id`/`camera_id` for joins.

---

## Debugging & common issues
- 401 on protected endpoints: verify `Authorization: Bearer <token>` and token validity (signature/expiration). Check logs from `AuthenticationFilter` / `SecurityConfig` for details.
- Flyway errors: ensure JDBC URL and credentials are set for Flyway and JDBC driver is present in `pom.xml`.
- R2DBC errors: check `spring.r2dbc.url` and that Postgres is listening on the configured port. Use `netstat -ano | findstr 5432` in `cmd` to check port usage on Windows.
- Repository method errors: if Spring Data can't create a query for a derived method (method name doesn't map to properties), either rename the method to match entity fields or use `@Query` with explicit SQL.

---

## Developer recommendations & next steps
- Centralize validation in `UserValidation` and `CameraValidation` services and keep controllers/services slim.
- Add `@ControllerAdvice` to standardize error responses (map exceptions to HTTP statuses and JSON bodies).
- Add integration tests that run Flyway migrations against a disposable test DB and verify core flows (register → login → call protected endpoint).
- Store `jwt.secret` securely (env variable, secret manager) and never commit it.
- Consider adding an OpenAPI/Swagger doc for easier testing and client generation.

---

## Where to find things in the code (exact files to look at)
- Security: `src/main/java/org/wildcloud/wildcloud_backend/security/JwtUtil.java`, `AuthenticationFilter.java`, `SecurityConfig.java`
- Controllers: `AuthenticationController.java`, `UserController.java`, `CameraController.java`, `RelationsController.java`
- Services: `service/` implementations and validation classes
- Migrations: `src/main/resources/db/migration/`

---

If you'd like, I can now:
- generate a Postman collection (JSON) for register/login/refresh/CRUD on cameras and users, or
- add a `README` section with direct sample requests for each endpoint and sample DTOs extracted from your code, or
- add a minimal `swagger` config so you can view live API docs.

Which of these should I do next?
