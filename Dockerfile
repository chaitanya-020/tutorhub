# --- build stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy pom + wrapper first so Maven can cache dependencies across rebuilds.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Now copy the source and build the jar. Skip tests in the image build:
# CI runs them separately, and Testcontainers needs Docker-in-Docker which
# we don't want during a deploy image build.
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# --- runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT at runtime; Spring Boot binds to it via SERVER_PORT.
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]