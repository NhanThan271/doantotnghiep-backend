package com.restaurant.doantotnghiep.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveImage(MultipartFile file);
}
