# Student Management System

Backend application for managing the educational process at a university.

## Features
- Role-based access control (Admin, Teacher, Student)
- Authentication and authorization with Spring Security (including OAuth2)
- REST API for managing:
  - Students
  - Teachers
  - Courses
  - Groups
  - Lessons
  - Faculties and Departments
- DTO mapping using MapStruct
- Logging and auditing using AOP
- CSV import functionality
- API documentation with Swagger
- Database migrations and structured schema
- Unit and integration testing

## Tech Stack
- Java
- Spring Boot
- Spring Security (OAuth2)
- Hibernate / JPA
- PostgreSQL
- JDBC
- MapStruct
- Swagger (OpenAPI)
- AOP (Spring AOP)
- JUnit / Mockito
- Docker

## Architecture
The application is built using a layered architecture:
- Controller layer (REST endpoints)
- Service layer (business logic)
- Repository layer (data access)
- DTO and Mapper layer (data transformation)

## Testing
- Unit tests using JUnit and Mockito
- Integration tests for controllers and services

## How to Run
1. Clone the repository
2. Configure application properties (DB, OAuth credentials)
3. Run PostgreSQL
4. Start the application:
   ```bash
   mvn spring-boot:run
