# 📜 Notification Service

## 🚀Overview
**Notification Service** chịu trách nhiệm gửi email thông báo cho người dùng dựa trên các sự kiện nhận được từ Apache Kafka. Hiện tại, service này xử lý các sự kiện thông báo liên quan đến cập nhật trạng thái phòng mà người dùng quan tâm (ví dụ: phòng trống trở lại).

## 🛠️Setup
- Được xây dựng bằng **Spring Boot** (Java).
- Sử dụng file `Dockerfile` (được cung cấp trong thư mục gốc của service) để container hóa.
- Mã nguồn chính nằm trong thư mục `src/`.

## 💻Development
- Cấu hình chính của service được định nghĩa trong `src/main/resources/application.yml` (hoặc `application.properties`), đặc biệt là cấu hình mail server và Kafka.
- Khởi chạy toàn bộ hệ thống (bao gồm service này) bằng lệnh `docker-compose up -d` từ thư mục gốc của dự án.

## 📡Endpoints
- Base URL: Service này không cung cấp bất kỳ HTTP endpoint nào cho client bên ngoài. Hoạt động của nó được điều khiển bởi các message trên Kafka.