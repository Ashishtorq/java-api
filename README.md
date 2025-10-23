# Task API (Java)

Small self-contained Java REST API for managing tasks.

Features

- GET /tasks - list all tasks

- GET /tasks/{id} - get task by id

- POST /tasks - create task (JSON body: title, description). status defaults to "pending"

- PUT /tasks/{id} - update task (JSON body: any of title, description, status)

Run (requires Java 11+ and Maven):

```powershell
cd 'c:/Users/ashis/OneDrive/Desktop/New folder/JAVA'
mvn package
java -jar target/task-api-1.0.0.jar
```

Notes

- The project uses Java's built-in HttpServer and an in-memory store; no external DB.

- If you don't have Maven installed, you can run from an IDE (import as Maven project) or use the included `run.ps1` helper to compile and run with `javac`/`java`.

Docker

Build the image (requires Docker):

```powershell
cd 'c:/Users/ashis/OneDrive/Desktop/New folder/JAVA'
docker build -t task-api:1.0 .
```

Run the container:

```powershell
docker run --rm -p 8000:8000 task-api:1.0
```

Next steps / improvements

- Add input validation and more tests

- Add logging and graceful shutdown

- Replace in-memory store with an embedded DB (H2/SQLite) if persistence is needed
"# java-api" 
