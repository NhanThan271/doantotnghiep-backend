package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class StorageServiceImpl implements StorageService {

    // Thư mục lưu ảnh trong project
    private final Path uploadDir = Paths.get("uploads");

    @Override
    public String saveImage(MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa có
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Tên file duy nhất
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Đường dẫn file lưu
            Path filePath = uploadDir.resolve(fileName);

            // Lưu file vào thư mục uploads
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Quan trọng: KHÔNG thêm "/" ở đầu đường dẫn
            return "uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file ảnh: " + e.getMessage());
        }
    }

}
