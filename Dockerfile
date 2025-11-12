FROM openjdk:17.0.2-jdk-slim
COPY build/libs/eyedia-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app.jar"]