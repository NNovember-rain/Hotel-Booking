# 🌐 API Gateway

## 🚀Overview
API Gateway đóng vai trò là cổng trung tâm trong hệ thống, chịu trách nhiệm định tuyến các yêu cầu từ client đến các microservices và xử lý xác thực người dùng thông qua Keycloak. Nó đảm bảo an toàn và thống nhất giao tiếp giữa frontend và backend trong kiến trúc SOA.

## 🛠️Setup
- Xây dựng bằng Spring Boot và Spring Cloud Gateway.
- Sử dụng file `Dockerfile` để container hóa.
- Mã nguồn chính nằm trong thư mục `src/`.

## 💻Development
- Cấu hình định tuyến được định nghĩa trong `application.yml`.
- Khởi chạy bằng cách chạy `docker-compose up --build` từ thư mục gốc của hệ thống.

## 📡Endpoints
- Base URL: `http://localhost:8088/`
- Các route được định tuyến đến:
    - `/profile/**` → Profile Service
    - `/booking/**` → Booking Service
    - `/hotel/**` → Hotel Service
    - `/notification/**` → Notification Service
    - `/history/**` → History Service
- Tích hợp bảo mật JWT với Keycloak.
