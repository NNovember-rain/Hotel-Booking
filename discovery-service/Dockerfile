FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/com-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
