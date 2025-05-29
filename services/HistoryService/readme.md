# 📜 History Service

## 🚀Overview
**History Service** chịu trách nhiệm lưu trữ và truy vấn lịch sử các sự kiện liên quan đến đặt phòng. Service này tiêu thụ các sự kiện từ Kafka (do Booking Service gửi) và lưu chúng vào cơ sở dữ liệu. Admin có thể truy vấn và quản lý các bản ghi lịch sử này.


## 🛠️Setup
- Được xây dựng bằng **Spring Boot** (Java).
- Sử dụng file `Dockerfile` (được cung cấp trong thư mục gốc của service) để container hóa.
- Mã nguồn chính nằm trong thư mục `src/`.

## 💻Development
- Cấu hình chính của service được định nghĩa trong `src/main/resources/application.yml`.
- Khởi chạy toàn bộ hệ thống (bao gồm service này) bằng lệnh `docker-compose up -d` từ thư mục gốc của dự án.

## 📡Endpoints
- Base URL: Thông qua API Gateway `http://localhost:8088/history/`