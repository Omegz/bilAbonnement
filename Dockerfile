# --- build stage ---
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -DskipTests package

# --- run stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app
ENV PORT=9090
EXPOSE 9090
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
