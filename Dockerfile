## Multi-stage Dockerfile
# Build with Maven then run the fat JAR on a slim JRE image

FROM maven:3.8.8-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:11-jre-jammy
WORKDIR /app
# Copy shaded jar produced by maven-shade-plugin
COPY --from=build /app/target/task-api-1.0.0.jar /app/task-api.jar

EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/task-api.jar"]
