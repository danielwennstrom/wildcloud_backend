# WildCloud Backend

Cloud platform for trail cameras. Upload and manage photos from your wildlife cameras.

Quick reference for running and working with the backend.
Frontend: https://github.com/danielwennstrom/wildcloud_frontend

---

## Stack

- **Framework**: Spring Boot with WebFlux (reactive)
- **Database**: PostgreSQL with R2DBC, Flyway for migrations
- **Auth**: JWT access + refresh tokens (refresh tokens partially implemented)
- **Build**: Maven

---

## Project layout

```
src/main/java/.../
├── controller/       # REST endpoints
├── service/          # Business logic
├── repository/       # R2DBC repositories
└── security/         # JWT utils, filters, security config

src/main/resources/
├── db/migration/     # Flyway SQL scripts
└── application.properties
```

---

## Auth flow

We use JWT for access tokens and refresh tokens stored server-side.

**Login**:

1. POST `/api/AuthenticateUsers/login` with email + password
2. Get back `accessToken` and `refreshToken`
3. Use `Authorization: Bearer <accessToken>` header on protected routes

**Refresh**:

- POST `/api/users/refreshToken` with your refresh token to get a new access token

**Logout**:

- POST `/api/users/logout` with your email to invalidate refresh tokens

Public endpoints are registration, login and uploading photos by e-mail. Everything else needs a valid JWT (check
`SecurityConfig` for specifics).

---

## Config

Keep secrets out of git. Use environment variables or a local `.env` file. Note that the Cloudflare variables are only
required if using the 'production' application profile, i.e. for using cloud storage to persist the photos.

Key properties:

```properties
POSTGRES_URL=jdbc:postgresql://localhost:5432/wildcloud
POSTGRES_R2DBC_URL=r2dbc:postgresql://localhost:5432/wildcloud
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=
CLOUDFLARE_R2_ACCOUNT_ID=
CLOUDFLARE_R2_ACCESS_KEY=
CLOUDFLARE_R2_SECRET_KEY=
CLOUDFLARE_R2_BUCKET_NAME=
CLOUDFLARE_R2_ENDPOINT=
CLOUDFLARE_R2_CUSTOM_DOMAIN=
SERVER_PORT=8080
JWT_SECRET=
```

**Note**: Flyway uses JDBC at startup (make sure the JDBC driver is in `pom.xml`), but the app uses R2DBC at runtime.

---

## Running locally

1. Start PostgreSQL and create the `wildcloud` database
2. Set your env variables (DB creds, `jwt.secret`, etc.)
3. Build and run:

```cmd
mvn clean package
mvn spring-boot:run
```

Or just run it from your IDE.

Flyway will handle migrations on startup—check the logs if something breaks.

---

## Testing with Postman

**Register**:

```
POST http://localhost:8080/api/AuthenticateUsers/createUser
Body:
{
    "userEmail": "alice@example.com",
    "password": "admin1",
    "firstName": "Alice",
    "lastName": "Example"
}
```

**Login**:

```
POST http://localhost:8080/api/AuthenticateUsers/login
Body:
{
    "userEmail": "alice@example.com",
    "password": "admin1"
}
```

Save the `accessToken` and `refreshToken` from the response.
**Use the token**:
Add header: `Authorization: Bearer <accessToken>`

**Refresh**:

```
POST http://localhost:8080/api/users/refreshToken
Body: 
{ 
    "refreshToken": "<your-refresh-token>" 
}
```

**Logout**:

```
POST http://localhost:8080/api/users/logout
Body: 
{ 
    "userEmail": "alice@example.com" 
}
```

---

## Cameras and user relations

Cameras are separate entities. For many-to-many relationships (users ↔ cameras), use a join table `user_camera` and the
`UserCamera` entity.

To get cameras for a user:

1. Query `user_camera` by `user_id`
2. Fetch each camera by `camera_id`
3. Map to DTOs and return as a `Flux` or `List`

**Tip**: Use numeric IDs for joins, not emails. IDs are immutable and way more efficient. Emails can change and will
mess up your relations.

---

## Common issues

**401 on protected routes**:

- Check your `Authorization: Bearer <token>` header
- Make sure the token isn't expired or invalid
- Look at `AuthenticationFilter` logs for details

**Flyway errors**:

- Verify your JDBC URL and credentials
- Make sure the JDBC driver is in `pom.xml`

**R2DBC errors**:

- Check `spring.r2dbc.url`
- Confirm Postgres is running: `netstat -ano | findstr 5432` (Windows)

**Repository method issues**:

- Spring Data can't auto-generate queries from method names that don't match entity fields
- Either rename the method or use `@Query` with explicit SQL

---

## Tips for maintainers

- Keep validation logic in `UserValidation` and `CameraValidation` services
- Add `@ControllerAdvice` for consistent error responses
- Write integration tests that use Flyway against a test database
- Never commit `jwt.secret`—use env vars or a secret manager
- Consider adding Swagger/OpenAPI docs for easier testing

---

## Key files

- **Security**: `JwtUtil.java`, `AuthenticationFilter.java`, `SecurityConfig.java`
- **Controllers**: `AuthenticationController.java`, `UserController.java`, `CameraController.java`,
  `RelationsController.java`
- **Services**: Everything in `service/`
- **Migrations**: `src/main/resources/db/migration/`