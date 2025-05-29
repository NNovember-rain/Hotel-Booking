# 📜 Hotel Service

## 🚀Overview
**Hotel Service** quản lý thông tin về Hotel (khách sạn) và các Room (phòng) trong Hotel. Service này cung cấp các API cho phép admin thực hiện thao tác CRUD (Tạo, Đọc, Cập nhật, Xóa) đối với khách sạn và phòng, bao gồm cả việc tải lên hình ảnh. Ngoài ra, Hotel Service còn xử lý yêu cầu kiểm tra tình trạng phòng từ Booking Service thông qua Kafka và cập nhật trạng thái phòng tương ứng.

## 🛠️Setup
- Được xây dựng bằng **Spring Boot** (Java).
- Sử dụng file `Dockerfile` (được cung cấp trong thư mục gốc của service) để container hóa.
- Mã nguồn chính nằm trong thư mục `src/`.

## 💻Development
- Cấu hình chính của service được định nghĩa trong `src/main/resources/application.yml` (hoặc `application.properties`).
- Khởi chạy toàn bộ hệ thống (bao gồm service này) bằng lệnh `docker-compose up -d` từ thư mục gốc của dự án.

## 📡Endpoints
- Base URL: Thông qua API Gateway `http://localhost:8088/hotel/`