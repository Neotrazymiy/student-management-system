# Student Management System

## About the project
This project simulates a university management system with role-based access and backend business logic.

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

## Live Demo
Application is available at:
http://your-url

Authentication is required.

## What I learned
- Designing layered backend architecture
- Implementing role-based access control
- Using AOP for cross-cutting concerns
- Working with DTO mapping (MapStruct)
- Writing unit and integration tests

## Quick Start
1. Clone the repository
2. Configure `application.properties`
3. Create PostgreSQL database
4. Run with:

```bash
mvn spring-boot:run
