package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.User;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
        User createUser(String username, String password, String fullName, String email, String phone, String role,
                        Long branchId,
                        MultipartFile image);

        User updateUser(Long id, String fullName, String email, String phone, String role, Long branchId,
                        Boolean isActive,
                        MultipartFile image);

        User getUserById(Long id);

        List<User> getAllUsers();

        void deleteUser(Long id);
}
