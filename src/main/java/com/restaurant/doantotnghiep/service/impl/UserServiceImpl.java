package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.enums.Role;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.service.StorageService;
import com.restaurant.doantotnghiep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User createUser(String username, String password, String fullName, String email, String phone, String role,
            Long branchId,
            MultipartFile image) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role != null ? Role.valueOf(role.toUpperCase()) : Role.EMPLOYEE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);

        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Chi nhánh không tồn tại với ID: " + branchId));
            user.setBranch(branch);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            user.setImageUrl(imageUrl);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, String fullName, String email, String phone, String role, Long branchId,
            Boolean isActive,
            MultipartFile image) {
        User existing = getUserById(id);
        existing.setFullName(fullName != null ? fullName : existing.getFullName());
        existing.setEmail(email != null ? email : existing.getEmail());
        existing.setPhone(phone != null ? phone : existing.getPhone());
        existing.setUpdatedAt(LocalDateTime.now());

        if (role != null) {
            existing.setRole(Role.valueOf(role.toUpperCase()));
        }

        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Chi nhánh không tồn tại với ID: " + branchId));
            existing.setBranch(branch);
        }

        if (isActive != null) {
            existing.setIsActive(isActive);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            existing.setImageUrl(imageUrl);
        }

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }
}
