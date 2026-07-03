FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-jar", "/app.jar"]
