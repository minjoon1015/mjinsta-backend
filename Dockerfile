FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY gradle ./gradle

RUN gradle build --no-daemon || return 0

COPY . .
RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
