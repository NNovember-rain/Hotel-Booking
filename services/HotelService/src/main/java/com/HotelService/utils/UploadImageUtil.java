package com.HotelService.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadImageUtil {

    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    public String upload(MultipartFile file) throws IOException {

        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);

        //TODO: Tạo tên tệp duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        int dotIndex = uniqueFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            uniqueFilename = uniqueFilename.substring(0, dotIndex); // Cắt bỏ phần mở rộng
        }
        //TODO: Tải ảnh
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", uniqueFilename,
                "use_filename", true,
                "unique_filename", false,
                "overwrite", false,
                "transformation", new Transformation()
                        .quality("auto")            //TODO: Nén ảnh tự động
                        .fetchFormat("auto")        //TODO: Định dạng ảnh tự động
                //.width(1000).crop("scale")        //TODO: Đặt lại kích thước
        ));

        //TODO: Lấy URL của hình ảnh đã tải lên Cloud
        return uploadResult.get("secure_url").toString();
    }
}