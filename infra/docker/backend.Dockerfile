# Build context: repo root.  docker build -f infra/docker/backend.Dockerfile -t impilohealth-api .
# Image is what gets pushed to Huawei SWR and deployed on CCE / ECS.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY backend/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 impilo
COPY --from=build /build/target/*.jar app.jar
USER impilo
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
