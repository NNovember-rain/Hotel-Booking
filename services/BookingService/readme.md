# 📜Booking Service 

## 🚀Overview
**Booking Service** chịu trách nhiệm quản lý toàn bộ quy trình đặt phòng khách sạn. Service này cho phép người dùng tạo yêu cầu đặt phòng, xử lý trạng thái đặt phòng dựa trên thông tin phòng từ Hotel Service, gửi thông báo và lưu trữ lịch sử đặt phòng thông qua Kafka.

Service được xây dựng bằng **Spring Boot** (Java) và giao tiếp với các service khác (Hotel Service, Profile Service) qua REST API (Feign Client) và các sự kiện Kafka.
## 🛠️Setup
- Được xây dựng bằng **Spring Boot** (Java).
- Sử dụng file `Dockerfile` (được cung cấp trong thư mục gốc của service) để container hóa.
- Mã nguồn chính nằm trong thư mục `src/`.

## 💻Development
- Cấu hình chính của service được định nghĩa trong `src/main/resources/application.yml` .
- Khởi chạy toàn bộ hệ thống (bao gồm service này) bằng lệnh `docker-compose up -d` từ thư mục gốc của dự án.

## 📡Endpoints
- Base URL: Thông qua API Gateway `http://localhost:8088/booking/`
