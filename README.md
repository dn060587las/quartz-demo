## Example of using Spring + Quartz

- Before start postgres on Docker:

```
docker pull postgres

docker run -d --name pg1 -e POSTGRES_PASSWORD=test  -p 5432:5432 postgres
```

- Run
```
mvn liquibase:update
```
