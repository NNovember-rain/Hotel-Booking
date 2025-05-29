# Dùng image OpenJDK chính thức
FROM openjdk:17-jdk-slim

# Tạo thư mục chứa app trong container
WORKDIR /app

# Copy file jar vào container
COPY target/com-0.0.1-SNAPSHOT.jar app.jar

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

# Mở cổng (tuỳ theo cấu hình server.port của bạn)
EXPOSE 8082