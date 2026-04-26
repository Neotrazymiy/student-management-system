# Student Management System

## About the project
This project is a full-featured backend system that simulates a university management platform with role-based access control, authentication, and complex business logic.

It demonstrates real-world backend development practices including layered architecture, security, data validation, and audit logging.

 ## Key Highlights

- Secure authentication with Spring Security and Google OAuth
- Clean layered architecture (Controller → Service → Repository)
- DTO mapping with MapStruct
- Audit logging using AOP
- CSV import for bulk operations

## Live Demo

The application is deployed on Render:

- Application: https://student-management-system-6eri.onrender.com
- Login page: https://student-management-system-6eri.onrender.com/login

### Live Status
⚠️ The application is hosted on Render (free tier), so the first request may take up to 30–60 seconds due to cold start.

## Demo Credentials

Admin access:

- Login: admin1  
- Password: pass5
 
Or use Google OAuth authentication.

## How to test

1. Open the link above
2. Log in using admin credentials or via Google OAuth
3. Explore system functionality (users, lessons, roles, etc.)

## Features
- Role-based access control (Admin, Teacher, Student)
- Authentication and authorization with Spring Security
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
- Database migrations
- Unit and integration testing

## Tech Stack
- Java
- Spring Boot
- Spring Security
- Hibernate / JPA
- PostgreSQL
- JDBC
- MapStruct
- AOP (Spring AOP)
- JUnit / Mockito

## Architecture
- Controller layer
- Service layer
- Repository layer
- DTO / Mapper layer

## What I learned
- Designing layered backend architecture
- Implementing role-based access control
- Using AOP for cross-cutting concerns
- Working with DTO mapping (MapStruct)
- Writing unit and integration tests

## Quick Start (Docker)

1. Clone the repository
```bash
git clone https://github.com/Neotrazymiy/student-management-system.git
cd student-management-system
```

2. Start PostgreSQL using Docker:
```bash
docker-compose up -d
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Open application:
http://localhost:8080/login
