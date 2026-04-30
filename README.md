# HRIS App

This project is a Spring Boot backend for a simple HRIS workflow. It handles authentication, employee management, attendance, and a small set of master data used by the application.

The codebase is set up for local development first. It uses PostgreSQL, JWT-based authentication, and Spring Security. On startup, the application will try to create the configured PostgreSQL database automatically if it does not already exist.

## What This App Covers

- Login and JWT authentication
- Initial admin account setup
- Employee CRUD-style operations
- Employee photo upload
- Attendance check-in, check-out, and absence status submission
- Automatic seeding for master data such as jabatan, departemen, unit kerja, pendidikan, jenis kelamin, and status absen

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)
- Lombok
- Maven

## Project Structure

The main packages are organized as follows:

- `com.hris.controller`: HTTP endpoints
- `com.hris.service`: business logic
- `com.hris.repository`: JPA repositories
- `com.hris.entity`: database entities
- `com.hris.config`: security and application bootstrap
- `com.hris.util`: helper utilities

## Configuration Profiles

Configuration is now split by environment:

- [`application.properties`](</home/karumakarumakaruma/Downloads/hris-app/src/main/resources/application.properties>) contains shared settings and defaults
- [`application-local.properties`](</home/karumakarumakaruma/Downloads/hris-app/src/main/resources/application-local.properties>) contains the local PostgreSQL connection
- [`application-prod.properties`](</home/karumakarumakaruma/Downloads/hris-app/src/main/resources/application-prod.properties>) expects database credentials from environment variables
- [`application-test.properties`](</home/karumakarumakaruma/Downloads/hris-app/src/main/resources/application-test.properties>) keeps test-specific overrides separate

The active profile is controlled by `APP_PROFILE`. If you do nothing, the app uses `local`.

Shared defaults:

- App name: `hris-app`
- Port: `8080`
- Hibernate mode: `update`
- Upload directory: `uploads/photos`

Local profile defaults:

- Database URL: `jdbc:postgresql://localhost:5432/hris_db`
- Database username: `postgres`
- Database password: `pass`

## Prerequisites

Before running the app, make sure you have:

- Java 17 installed
- Maven installed
- PostgreSQL installed and running on `localhost:5432`
- A PostgreSQL user that matches the active profile configuration

The app can create the `hris_db` database automatically, but it still needs a PostgreSQL user with permission to create databases. If the configured user does not have that privilege, startup will still fail.

## How To Run

From the project root:

```bash
mvn spring-boot:run
```

Or build first:

```bash
mvn clean package
java -jar target/hris-app-1.0.0.jar
```

## Startup Behavior

On startup, the application does a few things automatically:

1. It reads the datasource settings from the active Spring profile.
2. It checks whether the PostgreSQL database exists.
3. If the database is missing, it tries to create it.
4. Hibernate updates the schema with `spring.jpa.hibernate.ddl-auto=update`.
5. The app seeds default master data if those tables are still empty.

That means a fresh local database can usually be bootstrapped just by running the app, assuming PostgreSQL is available and the database user has the right permissions.

## Authentication Notes

This API uses JWT for authenticated endpoints.

Most write endpoints now accept JSON request bodies through `@RequestBody`. The main exception is photo upload, which still uses multipart form data.

Responses now follow a shared structure:

```json
{
  "success": true,
  "message": "Login berhasil.",
  "data": {},
  "errors": null,
  "timestamp": "2026-04-30T16:00:00Z"
}
```

For authenticated requests, send:

```text
Authorization: Bearer <your_token>
```

## Useful Endpoints

Auth:

- `POST /api/auth/init-data`
- `POST /api/auth/login`
- `POST /api/auth/ubah-password-sendiri`

Pegawai:

- `GET /api/pegawai/combo/jabatan`
- `GET /api/pegawai/combo/departemen`
- `GET /api/pegawai/combo/unit-kerja`
- `GET /api/pegawai/combo/pendidikan`
- `GET /api/pegawai/combo/jenis-kelamin`
- `GET /api/pegawai/combo/departemen-hrd`
- `GET /api/pegawai/daftar`
- `POST /pegawai/admin-tambah-pegawai`
- `POST /pegawai/admin-ubah-pegawai`
- `POST /pegawai/admin-ubah-photo`
- `POST /pegawai/ubah-photo`

Presensi:

- `GET /presensi/combo/status-absen`
- `GET /presensi/daftar/admin`
- `GET /presensi/daftar/pegawai`
- `GET /presensi/in`
- `GET /presensi/out`
- `POST /presensi/abseni`

## Roles And Permissions

There are two practical access levels in the current application.

`ADMIN`

- Can initialize the system
- Can log in
- Can view employee data
- Can create employee records
- Can update employee records
- Can update employee photos through the admin endpoint
- Can view admin attendance listings

`PEGAWAI`

- Can log in
- Can change their own password
- Can update their own photo
- Can check in
- Can check out
- Can submit attendance status for themselves
- Can view their own attendance history

`HRD access`

- There is no separate profile value for HRD in authorization checks
- A user is treated as HRD when their department name is `HRD`
- HRD gets the same access as admin for endpoints guarded by `roleChecker.getAdminOrHrd(...)`

Public endpoints:

- `POST /api/auth/init-data`
- `POST /api/auth/login`
- `GET /uploads/**`

## Example Curl Requests

Initialize first admin:

```bash
curl --request POST 'http://localhost:8080/api/auth/init-data' \
  --header 'Content-Type: application/json' \
  --data '{
    "namaAdmin": "Admin HRIS",
    "perusahaan": "MyCompany"
  }'
```

Login:

```bash
curl --request POST 'http://localhost:8080/api/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{
    "email": "admin@mycompany.com",
    "password": "yourpassword",
    "profile": "ADMIN"
  }'
```

Change your own password:

```bash
curl --request POST 'http://localhost:8080/api/auth/ubah-password-sendiri' \
  --header 'Authorization: Bearer YOUR_JWT_TOKEN' \
  --header 'Content-Type: application/json' \
  --data '{
    "passwordAsli": "oldpassword",
    "passwordBaru1": "newpassword123",
    "passwordBaru2": "newpassword123"
  }'
```

Get employee list:

```bash
curl --request GET 'http://localhost:8080/api/pegawai/daftar' \
  --header 'Authorization: Bearer YOUR_JWT_TOKEN'
```

Check in:

```bash
curl --request GET 'http://localhost:8080/presensi/in' \
  --header 'Authorization: Bearer YOUR_JWT_TOKEN'
```

Submit attendance status:

```bash
curl --request POST 'http://localhost:8080/presensi/abseni' \
  --header 'Authorization: Bearer YOUR_JWT_TOKEN' \
  --header 'Content-Type: application/json' \
  --data '{
    "tglAbsensi": 1714435200000,
    "kdStatus": 1
  }'
```

## File Upload Note

Photo upload endpoints use multipart form data. They do not accept JSON.

Example:

```bash
curl --request POST 'http://localhost:8080/pegawai/ubah-photo' \
  --header 'Authorization: Bearer YOUR_JWT_TOKEN' \
  --form 'namaFile=profile-photo' \
  --form 'files=@/path/to/photo.jpg'
```

## Development Notes

- Security is stateless and based on JWT.
- Public endpoints are currently limited to login, initial setup, and uploaded file access.
- JSON request payloads are handled through DTOs and validation annotations.
- Response bodies now use a consistent envelope for success and error cases.
- Configuration is split by Spring profile instead of being kept in a single file.

## Common Problems

If startup fails with a PostgreSQL error:

- Make sure PostgreSQL is running.
- Make sure the username and password in `application.properties` are valid.
- Make sure the PostgreSQL user can create databases if `hris_db` does not exist yet.

If a request fails with `401`:

- Confirm the JWT token is present.
- Confirm the token is sent as `Bearer <token>`.
- Confirm you are calling a non-public endpoint with a valid authenticated user.

If a request fails even though you sent JSON:

- Check whether the endpoint is one of the upload routes. Photo upload endpoints still expect multipart form data.
