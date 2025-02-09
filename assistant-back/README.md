# Running assistant-back

## In Docker

```bash
docker compose up --build -d
```

## Locally

```bash
docker compose up -d nsu_department_database
```

```bash
cd assistant-back/
```

```bash
./mvnw clean install -DskipTests
```

```bash
java -jar /target/department_assistant-0.0.1-SNAPSHOT.jar
```
