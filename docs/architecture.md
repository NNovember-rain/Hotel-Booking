# 🏗 System Architecture

## 🔍 Overview

Hệ thống đặt phòng khách sạn được xây dựng theo kiến trúc **SOA (Service-Oriented Architecture)** và triển khai theo mô hình **microservices**, giúp tách biệt chức năng và dễ dàng mở rộng. Hệ thống hỗ trợ:
- Quản lý người dùng và phân quyền (qua Keycloak)
- Quản lý khách sạn, phòng và đặt phòng
- Gửi thông báo sau khi hoàn tất quy trình đặt phòng
- Lưu trữ lịch sử 
- Giao tiếp nội bộ bằng Kafka 
- Hệ thống được giám sát và theo dõi bằng Spring Boot Admin và Zipkin.
---

## 🧩 System Components

### ✅ **Profile Service**
- Quản lý thông tin người dùng (khách hàng, quản trị viên)
- Cung cấp thông tin người dùng cho các service khác
- Không trực tiếp xử lý xác thực (ủy quyền cho Keycloak)

### 🏨 **Hotel Service**
- Quản lý danh sách khách sạn và các phòng
- Chức năng **Room Availability Service** để quản lý trạng thái phòng (trống, đã đặt)
- Cho phép tìm kiếm và cập nhật thông tin phòng

### 📆 **Booking Service**
- Xử lý toàn bộ quá trình đặt phòng
- Điều phối các bước  (đặt phòng → kiểm tra trạng thái phòng → gửi thông báo → lưu lịch sử)
- Giao tiếp với các service: `Profile`, `Hotel`, `Notification` và `History`

### 📢 **Notification Service**
- Gửi thông báo (email hoặc tin nhắn) cho người dùng
- Nhận các sự kiện từ Kafka như: `BookingConfirmed`, `BookingCancelled`

### 📜 **History Service**
- Lưu trữ và quản lý lịch sử các hoạt động quan trọng trong hệ thống, đặc biệt là lịch sử đặt phòng.


### 🌐 **API Gateway**
- Là cổng trung gian giữa client và các microservice
- Tích hợp với Keycloak để xác thực người dùng
- Định tuyến các request đến đúng service qua REST API



---
## 📚 Dịch Vụ Nền Tảng & Hỗ Trợ:
### 🔑 **Identity Provider (Keycloak)**
- Là dịch vụ trung tâm cho việc quản lý định danh và truy cập (IAM) của toàn hệ thống.
- Xử lý toàn bộ quá trình xác thực người dùng (đăng nhập, đăng ký, quên mật khẩu), quản lý phiên làm việc (sessions).
- Cấp phát và xác minh JWT (JSON Web Tokens) được sử dụng để bảo vệ các API và tài nguyên.
- Quản lý người dùng, các vai trò (roles), và các quyền (permissions) chi tiết.

### 🔄 **Discovery Service (Netflix Eureka)**
- Cho phép các microservice tự động đăng ký (register) khi khởi động và khám phá (discover) vị trí (địa chỉ IP, port) của các service khác trong môi trường động.
- Hỗ trợ client-side load balancing, giúp phân phối tải đều đến các instance của một service.
- Tăng tính linh hoạt và khả năng phục hồi của hệ thống khi các instance service có thể được thêm/bớt mà không cần cấu hình lại thủ công.
### 📨 **Message Broker (Apache Kafka)**
- Cho phép giao tiếp bất đồng bộ, dựa trên sự kiện (event-driven) giữa các microservice.
- Giúp tách rời (decouple) các service, tăng khả năng phục hồi (resilience) và cho phép xử lý các tác vụ nền mà không làm block luồng chính của người dùng.
- Lưu trữ các message (sự kiện) trong các topic, hỗ trợ đảm bảo thứ tự (trong một partition) và khả năng phát lại (replay) message khi cần.
### 🗃️ **MySQL Databases**
- Mỗi microservice nghiệp vụ sở hữu và quản lý cơ sở dữ liệu riêng của mình (theo Database-per-service pattern).
- Đảm bảo tính tự chủ về dữ liệu và schema cho từng service, cho phép lựa chọn loại CSDL phù hợp cho từng nhu cầu hiện tại sử dụng chủ yếu là MySQL.
- Hỗ trợ lưu trữ dữ liệu bền bỉ và nhất quán (trong phạm vi của từng service).
### 📊 **Monitor Admin Service (Spring Boot Admin)**
- Cung cấp giao diện đồ họa tập trung để giám sát health checks.
- Hỗ trợ nhận và hiển thị các cảnh báo qua Email khi một service gặp sự cố hoặc trạng thái thay đổi bất thường.
- Tương tác với các Actuator endpoint được cung cấp bởi các microservice Spring Boot.

### 📜 **Zipkin**
- Hệ thống theo dõi phân tán (distributed tracing) giúp trực quan hóa luồng xử lý của một yêu cầu khi nó đi qua nhiều microservice.
- Thu thập dữ liệu thời gian (timing data) để phân tích độ trễ (latency) và xác định các điểm tắc nghẽn (bottlenecks) trong hệ thống.

---
## 🔗 Communication

- **Giữa các service**:
    - **REST**: Dùng cho các thao tác kiểm tra, xác minh dữ liệu (đồng bộ)
    - **Kafka**: Dùng để truyền thông điệp 
- **Giao tiếp nội bộ**:
    - Qua Docker Compose hoặc Kubernetes: sử dụng tên service để gọi nội bộ (VD: `http://hotel-service:8080`)

---

## 🔄 Data Flow

1. Client gửi yêu cầu đặt phòng đến **API Gateway**
2. Gateway xác thực người dùng với Keycloak, sau đó định tuyến yêu cầu đến **Booking Service**
3. Booking Service:
    - Gọi `Profile Service` để xác minh người dùng
    - Gọi `Hotel Service` để kiểm tra tình trạng phòng trống
    - Tạo bản ghi đặt phòng tạm thời và gửi sự kiện `BookingCreated` qua Kafka
4. `Booking Service` xử lý kết quả thanh toán:
    - Nếu thành công → gửi `BookingConfirmed`
    - Nếu thất bại → gửi `BookingCancelled`
5. `Notification Service` nhận các topic sự kiện và gửi thông báo tương ứng đến người
6. `History Service` lắng nghe topic và lưu trữ thông tin chi tiết vào cơ sở dữ liệu lịch sử

---

## 🖼 Diagram

> Lưu ý: Đặt sơ đồ tổng thể kiến trúc tại `docs/assets/system_architecture.png`

---

## 📈 Scalability & Fault Tolerance

- **Scalability**:
  - Các `microservice` có thể được nhân bản (scaled horizontally) một cách độc lập bằng cách tăng số lượng replica (ví dụ: trong `Docker Compose` với docker-compose scale service_name=X, hoặc tự động trong Kubernetes) dựa trên nhu cầu tải. `Eureka` hỗ trợ việc khám phá các instance mới này.
  - `Apache Kafka` đóng vai trò là một bộ đệm (buffer) và hệ thống phân phối tải, cho phép các service xử lý sự kiện với tốc độ riêng, tăng khả năng chịu tải và cho phép xử lý theo lô (batch processing) nếu cần.
  - `Client-side` load balancing (thường được cung cấp bởi các thư viện tích hợp với `Eureka` như `Spring Cloud LoadBalancer`) hoặc load balancing ở `API Gateway` giúp phân phối request đều đến các instance của service.

- **Fault Tolerance**:
  - `Apache Kafka` đảm bảo độ bền của thông điệp (message persistence), đảm bảo thứ tự (trong một partition), và khả năng phát lại (replay) thông điệp nếu service tiêu thụ gặp lỗi và cần xử lý lại. Hỗ trợ cơ chế retry và Dead-Letter Queue (DLQ) cho các message không thể xử lý.
  - `Discovery Service` `(Eureka)` cho phép các client và `API Gateway` tự động phát hiện và loại bỏ các `instance service` bị lỗi khỏi việc định tuyến, chuyển request sang các `instance` còn khỏe mạnh.
  - `API Gateway` và các `microservice` có thể triển khai các mẫu thiết kế chịu lỗi như Circuit Breaker (ví dụ: sử dụng Resilience4j) để ngăn chặn lỗi lan truyền (cascading failures) và cho phép hệ thống phục hồi một cách duyên dáng khi một service phụ thuộc gặp sự cố.
  - Việc `mỗi service có database riêng` giúp cô lập sự cố ở tầng dữ liệu.
  - `Zipkin` giúp nhanh chóng xác định nguồn gốc của lỗi hoặc độ trễ trong các giao dịch phân tán.
  - `Monitor Admin Service` cung cấp cảnh báo sớm về các vấn đề sức khỏe của service.
