# 📜Discovery Service

## 🚀Tổng quan

**Dịch vụ Discovery** xử lý việc phát hiện dịch vụ trong toàn bộ kiến trúc microservices. Nó cho phép các dịch vụ tìm và giao tiếp với nhau một cách động. Dịch vụ này được xây dựng với **Spring Boot** và tích hợp với **Eureka** để đăng ký và phát hiện dịch vụ.

## 🛠️Cài đặt

* Được xây dựng bằng cách sử dụng `Dockerfile` đã cung cấp.
* Mã nguồn nằm trong thư mục `src/`.

## 💻Phát triển

* Chạy dịch vụ cục bộ bằng lệnh `docker-compose up --build` từ thư mục gốc.

## 📡Các Endpoints

* **URL cơ sở**: `http://localhost:8761/`
* **Bảng điều khiển Eureka**: Truy cập bảng điều khiển phát hiện dịch vụ Eureka tại `http://localhost:8761/` để xem tất cả các dịch vụ đã đăng ký.


## 🔗Cấu hình

* Dịch vụ phát hiện được cấu hình sử dụng **Eureka**. Mỗi microservice cần đăng ký với Dịch vụ Discovery khi khởi động.
* Dịch vụ Discovery sử dụng **Spring Cloud Eureka Server** để quản lý việc đăng ký dịch vụ.

## Docker

* Để chạy Dịch vụ Discovery qua Docker, thực hiện các lệnh sau:

  ```bash
  docker-compose up --build
  ```
* Lệnh này sẽ xây dựng và khởi động container với Dịch vụ Discovery chạy trên cổng `8761`.

## Ghi log

* Log có thể truy cập qua log của container hoặc đầu ra chuẩn.

## Khắc phục sự cố

* Nếu dịch vụ không đăng ký, hãy đảm bảo rằng cấu hình `eureka.client.serviceUrl.defaultZone` đã được thiết lập đúng trong các tệp cấu hình của ứng dụng.
