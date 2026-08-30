# Employee Task Manager — Full Stack

A full-stack rewrite of the original Employee Task Manager: **Spring Boot + Spring Data JPA + H2/MySQL** on the
backend, talking over a real REST API to the **HTML/CSS/JS** frontend (served by Spring Boot itself, so it's a
single app you run with one command).

## What changed from the original

The original project was a static HTML mockup — logins were hardcoded in JavaScript (`admin`/`1234`,
`employee`/`abcd`), "assigning a task" just added an `<li>` to the DOM, and nothing was ever saved. The
`TaskManager.java` file was a separate, disconnected console demo that never talked to the frontend at all.

This version connects everything into one real application:

- **Real backend** — Spring Boot REST API with layered `controller` → `service` → `repository` architecture.
- **Real database** — Spring Data JPA + H2 (in-memory, zero setup) with a one-line switch to MySQL.
- **Real auth** — passwords are hashed with BCrypt, never stored or compared in plain text.
- **Real persistence** — tasks assigned by the admin are saved to the database and immediately visible to the
  correct employee on login.
- **New features** — admin can create new employee accounts, employees can update their own task status
  (Pending / In Progress / Completed), admin can delete tasks, and the UI shows live counts and status colors.

## Project structure

```
task-manager/
├── pom.xml
├── src/main/java/com/taskmanager/
│   ├── TaskManagerApplication.java   # entry point
│   ├── model/                        # User, Task, Role, TaskStatus (JPA entities/enums)
│   ├── repository/                   # Spring Data JPA repositories
│   ├── service/                      # business logic (AuthService, TaskService)
│   ├── controller/                   # REST endpoints (AuthController, TaskController)
│   ├── dto/                          # request/response payloads
│   ├── config/                       # SecurityConfig (BCrypt), DataInitializer (seed data)
│   └── exception/                    # centralized error handling
└── src/main/resources/
    ├── application.properties        # H2 by default, MySQL instructions included
    └── static/                       # index.html, style.css, script.js (served at "/")
```

## Running it

You need **Java 17+** and **Maven** installed.

```bash
cd task-manager
mvn spring-boot:run
```

Then open **http://localhost:8080** in your browser. That's it — no separate frontend server, no database
setup required. Data lives in an in-memory H2 database and resets each time you restart the app.

### Demo accounts (seeded automatically on first run)

| Portal   | Username | Password |
|----------|----------|----------|
| Admin    | admin    | 1234     |
| Employee | employee | abcd     |
| Employee | mark     | abcd     |

You can also create brand-new employee accounts from the Admin dashboard.

### Switching to MySQL

1. In `pom.xml`, uncomment the `mysql-connector-j` dependency.
2. In `application.properties`, comment out the H2 block and uncomment the MySQL block, then fill in your
   database name / username / password.
3. Run `mvn spring-boot:run` again — Hibernate will create the tables automatically.

## REST API

| Method | Endpoint                       | Description                                |
|--------|---------------------------------|---------------------------------------------|
| POST   | `/api/auth/login`               | `{ username, password, role }` → user info  |
| GET    | `/api/employees`                | List all employees                          |
| POST   | `/api/employees`                | Create a new employee account               |
| GET    | `/api/tasks`                    | All tasks (admin view)                      |
| GET    | `/api/tasks/employee/{username}`| Tasks assigned to one employee               |
| POST   | `/api/tasks`                    | Assign a new task                            |
| PATCH  | `/api/tasks/{id}/status`        | Update a task's status                       |
| DELETE | `/api/tasks/{id}`               | Delete a task                                |

All responses are wrapped as `{ success, message, data }`.

## Notes / next steps

This is a learning-scale demo, not a hardened production app. If you wanted to take it further:
- Add real session/JWT-based authentication and lock down endpoints per role (currently the API trusts the
  frontend, similar to the original project's approach, just with hashed passwords and real storage now).
- Add pagination and search on the task list.
- Add email notifications when a task is assigned or completed.
