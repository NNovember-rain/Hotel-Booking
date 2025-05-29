package com.HotelService.dto.request;

import com.HotelService.entity.Room;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class HotelRequest {
    String name;
    String address;
    String phone;
    String email;
    String description;
    MultipartFile image;
}
