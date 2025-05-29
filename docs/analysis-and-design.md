# 🏨 Hệ thống Đặt Phòng Khách Sạn - Phân Tích & Thiết Kế

Tài liệu này mô tả quá trình phân tích và thiết kế hệ thống đặt phòng khách sạn sử dụng kiến trúc hướng dịch vụ (SOA) và các mẫu thiết kế microservices. Hệ thống được thiết kế để đảm bảo tính module hóa, khả năng mở rộng độc lập và dễ bảo trì.

---

## 1. 🎯 Vấn Đề Cần Giải Quyết

### Bài toán:
Xây dựng một hệ thống đặt phòng khách sạn trực tuyến, cho phép người dùng tìm kiếm, xem thông tin và đặt phòng một cách nhanh chóng, an toàn và tiện lợi. Hệ thống cũng cung cấp công cụ cho quản trị viên để quản lý khách sạn, phòng, đặt phòng và người dùng.

### Người dùng:
- Khách hàng (User/Customer): Tìm kiếm khách sạn, xem chi tiết phòng, đặt phòng, xem lịch sử đặt phòng, quản lý hồ sơ cá nhân.
- Quản trị viên (Admin): Quản lý thông tin khách sạn, loại phòng, phòng, quản lý đặt phòng, quản lý người dùng, xem báo cáo thống kê.

### Mục tiêu chính:
- Cung cấp giao diện trực quan cho khách hàng và quản trị viên.
- Hiển thị thông tin khách sạn và phòng trống một cách chính xác.
- Cho phép người dùng đặt phòng và nhận xác nhận (ví dụ: qua email) một cách tự động.
- Đảm bảo an toàn thông tin người dùng và giao dịch thông qua xác thực và phân quyền mạnh mẽ.
- Lưu trữ và cho phép truy vấn lịch sử đặt phòng và các hoạt động quan trọng khác.
- Hệ thống có khả năng mở rộng, theo dõi và dễ dàng bảo trì.

---

## 2. 🧩 Các Microservices

| Tên Dịch Vụ              | Trách Nhiệm Chính                                                                                                  | Công Nghệ                  |
|--------------------------|--------------------------------------------------------------------------------------------------------------------|----------------------------|
| **Booking Service**      | Quản lý quy trình đặt phòng (tạo, cập nhật trạng thái), xử lý nghiệp vụ đặt phòng, giao tiếp với các dịch vụ khác. | Spring Boot, Kafka         |
| **Profile Service**      | Quản lý người dùng và xác thực (tài khoản, phân quyền, JWT, đăng nhập/đăng ký)                                     | Spring Boot + Keycloak     |
| **Hotel Service**        | Quản lý thông tin khách sạn, loại phòng, phòng, và xử lý logic kiểm tra tình trạng phòng. (Room Availability)      | Spring Boot                |
| **Notification Service** | Gửi thông báo (email, SMS, etc.) cho người dùng về các sự kiện quan trọng (đặt phòng thành công/thất bại).         | Spring Boot, Mail, Kafka          |
| **History Service**      | Lưu trữ và truy vấn lịch sử đặt phòng của người dùng                                                               | Spring Boot                |
| **Discovery Service**    | Quản lý tìm kiếm và đăng ký service (Eureka)                                                                       | Spring Boot Netflix Eureka |
| **Monitor Admin Service**    | Cung cấp giao diện giám sát sức khỏe, metrics, logs, và các thông tin vận hành khác của các microservice           | Spring Boot Admin |
| **API Gateway**          | Giao tiếp chính giữa client và các service, định tuyến request                                                     | Spring Cloud Gateway       |

---

## 3. 🔄 Giao tiếp giữa các dịch vụ

- **API Gateway** ⇄ Tất cả dịch vụ (REST)
- **Booking Service**:
  - Gọi **Hotel Service** để kiểm tra phòng trống
  - Gọi **Profile Service** để xác minh người dùng
  - Gọi **Notification Service** để gửi thông báo
  - Gọi **History Service** để ghi nhận lịch sử đặt phòng
- **Profile Service** xử lý đăng ký, đăng nhập, tạo token, kiểm tra phân quyền
- **Hệ thống giám sát** bằng Spring Boot Admin và Zipkin.
---

## 4. 🗂️ Thiết Kế Dữ Liệu

### Profile Service:
- `users`: id, username, password, full_name, email, role
- API: đăng ký, đăng nhập (JWT), phân quyền, quản lý thông tin người dùng

### Hotel Service:
- `hotels`: id, name, address, description
- `rooms`: id, room_code, type, price, status (AVAILABLE, BOOKED), hotel_id

### Booking Service:
- `bookings`: id, user_id, room_id, price, date, check_in, check_out, status (PENDING, CONFIRMED)

### History Service:
- `booking_history`: id, booking_id, user_id, timestamp, action

### Notification Service:
- Không lưu dữ liệu – chỉ gửi thông báo realtime hoặc qua email

---

## 5. 🔐 Bảo Mật

- Dùng **JWT** do Profile Service phát hành để xác thực người dùng
- Mỗi service đều kiểm tra JWT qua API Gateway (hoặc trực tiếp)
- Phân quyền theo vai trò: `ROLE_USER`, `ROLE_ADMIN`
- Profile Service sử dụng Keycloak 

---

## 6. 📦 Kế hoạch triển khai

- Dùng `docker-compose` để chạy toàn bộ hệ thống
- Mỗi service có Dockerfile riêng
- Biến môi trường và config đặt trong `.env` hoặc `application.yml`
- Eureka để các service tự phát hiện lẫn nhau
- Hỗ trợ mở rộng lên cloud (AWS/GCP) hoặc Kubernetes sau này

---

## 7. 🧭 Sơ đồ kiến trúc

                                
                                              +---------------+
                                              |    Client     |
                                              |               |   
                                              +-------+-------+   
                                                      |           
                                                      v           
                                             +--------+--------+ 
                                             |                 | 
                                             |   API Gateway   | 
                                             |                 | 
                                             +--------+--------+ 
                                                      |          
              +---------------------------------------+---------------------------+
              |                     |                 |                           |
              v                     v                 v                           v
        +-----+-------+      +------+------+    +-----+-------+          +--------+--------+
        |             |      |             |    |              |         |                 |
        |  Keycloak   |<---->|  Profile    |<-->|   Booking    |<------->|     Hotel       |
        | (Auth/IAM)  |      |  Service    |    |   Service    |         |     Service     |
        |             |      |             |    |              |         |                 |
        +-------------+      +-------------+    +------+-------+         +-----------------+
                                                       |
                                   +-------------------+-----------------+
                                   |                                     |
                                   v                                     v
                          +--------+---------+               +-----------+-------+
                          |                  |               |                   |
                          |  Notification    |               |     History       |
                          |  Service         |               |     Service       |
                          |                  |               |                   |
                          +--------+---------+               +---------+---------+
                                   |                                  
                                   v                            
                          +--------+---------+               
                          |                  |             
                          |    Email/SMS     |              
                          |                  |              
                          +------------------+               
                      
    +-------------------+   +------------------+   +-------------------+   +------------------+   +------------------+   
    |                   |   |                  |   |                   |   |                  |   |                  | 
    | Discovery Service |   |   Apache Kafka   |   |  Spring Boot      |   |     Zipkin       |   |       MySQL      | 
    |     (Eureka)      |   |     (Events)     |   | Admin(Monitoring) |   |   (Distributed   |   |    (Database)    |
    |                   |   |                  |   |                   |   |     Tracing)     |   |                  |
    +-------------------+   +------------------+   +-------------------+   +------------------+   +------------------+  
              ^                      ^                      ^                       ^                       ^
              |                      |                      |                       |                       |
              +----------------------+----------------------+-----------------------+-----------------------+
                                                            |
                                                  +---------+---------+
                                                  |                   |
                                                  |  Infrastructure   |
                                                  |     Services      |
                                                  |                   |
                                                  +-------------------+
