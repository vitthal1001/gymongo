# Multi-stage Dockerfile: build with Gradle (no local Gradle required), then run minimal JRE image
FROM gradle:8.4-jdk17 AS builder
WORKDIR /home/gradle/project
# Copy everything and build the application (skip tests to speed up builds)
COPY --chown=gradle:gradle . /home/gradle/project
# Diagnostic: list java source tree to help debug missing-package compile errors
RUN echo "--- project tree ---" && ls -la /home/gradle/project || true
RUN echo "--- src tree ---" && ls -la /home/gradle/project/src || true
RUN echo "--- java tree ---" && ls -la /home/gradle/project/src/main/java || true
RUN echo "--- com/gymongo tree ---" && ls -la /home/gradle/project/src/main/java/com/gymongo || true
RUN echo "--- entities tree ---" && ls -la /home/gradle/project/src/main/java/com/gymongo/entity || true
RUN echo "--- repositories tree ---" && ls -la /home/gradle/project/src/main/java/com/gymongo/repository || true
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built jar from the builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
