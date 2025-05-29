# 📜Profile Service

## 🚀Overview
Profile Service là một microservice chịu trách nhiệm quản lý thông tin hồ sơ người dùng. Service này sử dụng **Keycloak** để xử lý xác thực và phân quyền người dùng. Service này được xây dựng bằng **Spring Boot**.

## 🛠️Setup
Để thiết lập Profile Service, bạn cần xây dựng và chạy service bằng cách sử dụng Docker.

- **Sử dụng Docker**: Dự án có sẵn `Dockerfile` để xây dựng image Docker cho Profile Service.
- **Thư mục mã nguồn**: Mã nguồn của Profile Service nằm trong thư mục `src/`.

Trước khi chạy Profile Service, cần có Keycloak để xử lý xác thực. Keycloak có thể chạy trong Docker hoặc kết nối với một Keycloak instance đã có sẵn.

### Cài đặt Keycloak
Sử dụng Docker để chạy Keycloak bằng lệnh sau:

```bash
`docker run -d --name keycloak-25.0.0 -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:25.0.0 start-dev`
```

Keycloak sẽ được khởi động tại `http://localhost:8180`. Bạn cần tạo một realm và client trong Keycloak để Profile Service có thể sử dụng.

### Cấu hình Keycloak
1. **Tạo một Realm**: Tạo một realm, ví dụ: `hotel`.
2. **Tạo Client**: Tạo một client trong realm này với các thông tin:
    - **Client ID**
    - **Client Secret**
    - **Redirect URI**
3. **Cấu hình Roles**:
    - `USER` (Người dùng cơ bản)
    - `ADMIN` (Quản trị viên)

## 💻Development
Để phát triển Profile Service, có thể chạy dịch vụ trong môi trường phát triển cục bộ thông qua Docker Compose.

1. **Docker Compose**: Chạy service bằng lệnh sau từ thư mục gốc của dự án:

    ```bash
    docker-compose up --build
    ```

   Điều này sẽ tạo và khởi động các container cho Profile Service và các dịch vụ phụ trợ khác nếu có.

2. **Cấu hình Keycloak cho Spring Boot**: Đảm bảo Profile Service có thể giao tiếp với Keycloak bằng cách cấu hình  `application.yml` của Spring Boot như sau:

```yaml
keycloak:
    client:
        id: hotel
        secret: FzdbNSBwMxFV2Xteo50Nh2Ues2acvbYX
```

## 📡Endpoints
- **Base URL**: `http://localhost:8085/  
- API chi tiết có thể tham khảo trong file `docs/api-specs/profile-service.yaml`.


