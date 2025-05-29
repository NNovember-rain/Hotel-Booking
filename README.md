# 🚀🏨 Hệ thống Đặt Phòng Khách Sạn

Đây là một dự án xây dựng hệ thống đặt phòng khách sạn dựa trên kiến trúc microservices. Hệ thống cho phép người dùng tìm kiếm khách sạn, xem thông tin phòng, đặt phòng, nhận thông báo xác nhận, và quản lý lịch sử đặt phòng. Quản trị viên có thể quản lý khách sạn, phòng, đặt phòng và người dùng. Hệ thống được thiết kế để đảm bảo tính module hóa, khả năng mở rộng độc lập và dễ bảo trì, với các tương tác bất đồng bộ qua Apache Kafka và giám sát, theo dõi bằng Spring Boot Admin, Zipkin.

## 📁 Folder Structure

```
mid-project-785341/
├── .idea/                             # Thư mục cấu hình của IntelliJ IDEA (hoặc IDE tương tự)
├── client/                            # Thư mục chứa mã nguồn Frontend (Giao diện người dùng)
├── docs/                              # Thư mục tài liệu của dự án
│   ├── architecture.md                # Mô tả kiến trúc hệ thống tổng thể
│   ├── analysis-and-design.md         # Tài liệu phân tích và thiết kế chi tiết
│   └── api-specs/                     # Đặc tả API theo OpenAPI (YAML) cho các service
│       ├── booking-service.yaml
│       ├── history-service.yaml
│       ├── hotel-service.yaml
│       ├── profile-service.yaml
│       └── service-b.yaml    
├── scripts/                           # Các script tiện ích hoặc triển khai
│   └── init.sh      
├── gateway/                           # Mã nguồn cho API Gateway (ví dụ: Spring Cloud Gateway)
│   ├── Dockerfile                     # (Giả định gateway có Dockerfile)
│   └── src/                           # (Mã nguồn Java/Kotlin của Gateway)       
├── monitor-admin/                     # Mã nguồn cho Monitor Admin Service (ví dụ: Spring Boot Admin)
│   ├── Dockerfile
│   └── src/
├── discovery-service/                 # Mã nguồn cho Discovery Service (Eureka)
│   ├── Dockerfile
│   └── src/                             
├── services/                          # Thư mục chứa mã nguồn của các microservices nghiệp vụ
│   ├── BookingService/
│   │   ├── Dockerfile                 
│   │   ├── src/                       
│   │   └── readme.md                  
│   ├── HistoryService/
│   │   ├── Dockerfile
│   │   ├── src/
│   │   └── readme.md
│   ├── HotelService/
│   │   ├── Dockerfile
│   │   ├── src/
│   │   └── readme.md
│   ├── NotificationService/
│   │   ├── Dockerfile
│   │   ├── src/
│   │   └── readme.md
│   └── ProfileService/
│       ├── Dockerfile
│       ├── src/
│       └── readme.md
├── .env.example                       # File ví dụ cho các biến môi trường
├── docker-compose.yml                 # File cấu hình Docker Compose để chạy toàn bộ hệ thống
├── local.env                          # File biến môi trường cục bộ (thường được gitignore)
└── README.md                          # File README chính của dự án

```

---

## 🚀 Getting Started

### Yêu cầu hệ thống
- **Docker** và **Docker Compose**
- **Java 11+**
- **Maven 3.6+**
- **Git**


1. **Clone this repository**

   ```bash
   git clone https://github.com/jnp2018/mid-project-785341.git
   cd mid-project-785341
   ```

2. **Copy environment file**
- Sao chép file .env.example thành local.env (hoặc .env tùy theo cấu hình docker-compose.yml).
   ```bash
   cp .env.example local.env
   ```
3. **Run with Docker Compose**

   ```bash
   docker-compose up --build
   ```
---

## 🌟 Các Thành phần Dịch vụ (Microservices) của Dự án

1. **Profile Service (profile-service):**
* **Công nghệ**: Spring Boot
* **Vai trò**: Quản lý thông tin hồ sơ người dùng (tên, địa chỉ, điện thoại,...). Không trực tiếp xử lý xác thực (việc này do Keycloak đảm nhiệm). Cung cấp API để các service khác hoặc người dùng (đã xác thực) truy xuất/cập nhật thông tin hồ sơ.

2. **Hotel Service (hotel-service):**
* **Công nghệ**: Spring Boot, Apache Kafka (Consumer/Producer)
* **Vai trò**: Quản lý thông tin khách sạn, loại phòng, phòng cụ thể. Xử lý logic kiểm tra tình trạng phòng trống (Room Availability) dựa trên các sự kiện từ Kafka và cập nhật trạng thái phòng.

3. **Booking Service (booking-service):**
* **Công nghệ**: Spring Boot, Apache Kafka (Consumer/Producer)
* **Vai trò**: Điều phối quy trình đặt phòng. Tiếp nhận yêu cầu, khởi tạo kiểm tra phòng qua Kafka, xử lý thanh toán (nếu có), xác nhận/hủy. Gửi sự kiện lên Kafka cho Notification Service, History Service, và Hotel Service.

3. **Notification Service (notification-service):**
* **Công nghệ**: Spring Boot, Spring Mail, Apache Kafka (Consumer)
* **Vai trò**: Gửi thông báo (email) cho người dùng về các sự kiện như đặt phòng thành công/thất bại. Lắng nghe sự kiện từ Kafka.

4. **History Service (history-service):**
* **Công nghệ**: Spring Boot, Apache Kafka (Consumer)
* **Vai trò**: Lưu trữ lịch sử đặt phòng và các hoạt động quan trọng. Nhận dữ liệu qua sự kiện Kafka. Cung cấp API cho quản trị viên tra cứu.

5. **API Gateway (gateway):**
* **Công nghệ**: Spring Cloud Gateway
* **Vai trò**: Điểm vào duy nhất cho client. Tích hợp Keycloak để xác thực JWT. Định tuyến request đến microservice nội bộ.

6. **Identity Provider (keycloak):**
* **Công nghệ**: Keycloak (Docker image: quay.io/keycloak/keycloak)
* **Vai trò**: Quản lý định danh và truy cập (IAM). Xử lý đăng nhập, đăng ký, cấp phát JWT, quản lý user, roles.

7. **Discovery Service (discovery-service):**
* **Công nghệ**: Spring Boot với Netflix Eureka Server
* **Vai trò**: Cho phép các microservice tự động đăng ký và khám phá lẫn nhau. Hỗ trợ client-side load balancing.

8. **Message Broker (kafka, zookeeper):**
* **Công nghệ**: Apache Kafka và Zookeeper (Docker images: confluentinc)
* **Vai trò**: Nền tảng giao tiếp bất đồng bộ, dựa trên sự kiện. Topics chính: hotel.checkRoomAvailability.requests, hotel.roomAvailability.responses, notification.send, history.log, hotel.room.booked.

9. **Database (mysql-db):**
* **Công nghệ**: MySQL (Docker image: mysql:8.0)
* **Vai trò**: Lưu trữ dữ liệu cho các microservice (Profile, Hotel, Booking, History). Mỗi service có schema/database riêng (database-per-service).

10. **Monitor Admin Service (monitor-admin):**
* **Công nghệ**: Spring Boot Admin Server
* **Vai trò**: Giao diện giám sát sức khỏe, metrics, logs, env, threads của các microservice Spring Boot.

11. **Distributed Tracing (zipkin-follow):**
* **Công nghệ**: Zipkin (Docker image: openzipkin/zipkin)
* **Vai trò**: Thu thập và trực quan hóa dữ liệu trace. Giúp phân tích độ trễ, gỡ lỗi trong hệ thống phân tán.

## 🧪 Development Notes

- Sử dụng các tệp (file) trong thư mục `docs/api-specs/*.yaml` để mô tả các API REST theo định dạng OpenAPI (Swagger).

---

