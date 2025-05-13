# 1단계: Gradle 빌드 단계
FROM gradle:8.1.0-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN chmod +x gradlew
RUN ./gradlew bootJar

# 2단계: 실행 이미지
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
