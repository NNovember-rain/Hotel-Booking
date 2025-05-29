# 📜Monitor Admin Service

Monitor Admin là một service sử dụng **Spring Boot Admin** để giám sát các microservice trong hệ thống, bao gồm trạng thái, health, metrics, logs, trace,...

## 🧩 Tính năng chính

- Giám sát các microservice đăng ký với Eureka
- Xem thông tin `health`, `env`, `metrics`, `logs`, `trace` của từng service
- Gửi thông báo lỗi qua email khi một service bị down
- Tích hợp với Zipkin để theo dõi trace

## 🚀 Công nghệ sử dụng

- Java 17
- Spring Boot Admin
- Spring Cloud Eureka Client
- Spring Boot Actuator
- Docker
- Zipkin (tùy chọn)
- Gmail SMTP (gửi email)

