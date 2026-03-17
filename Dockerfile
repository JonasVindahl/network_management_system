# ---- Build Stage ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Cache dependencies separately from source
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -q

# Build the jar
COPY src src
RUN ./mvnw package -DskipTests -q

# ---- Runtime Stage ----
FROM eclipse-temurin:25-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
