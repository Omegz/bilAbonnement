# --- build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -DskipTests package

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Render sætter PORT env var. Spring skal lytte på den.
ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
