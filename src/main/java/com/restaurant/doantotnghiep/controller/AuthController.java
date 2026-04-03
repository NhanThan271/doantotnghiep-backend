package com.restaurant.doantotnghiep.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.doantotnghiep.dto.LoginDto;
import com.restaurant.doantotnghiep.dto.RegisterDto;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.enums.Role;
import com.restaurant.doantotnghiep.entity.enums.StaffPosition;
import com.restaurant.doantotnghiep.entity.enums.StaffStatus;
import com.restaurant.doantotnghiep.payload.response.JwtResponse;
import com.restaurant.doantotnghiep.payload.response.MessageResponse;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.security.jwt.JwtUtils;
import com.restaurant.doantotnghiep.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final UserRepository userRepository;
        private final StaffRepository staffRepository;
        private final BranchRepository branchRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;

        @Autowired
        public AuthController(UserRepository userRepository,
                        StaffRepository staffRepository,
                        BranchRepository branchRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils) {
                this.userRepository = userRepository;
                this.staffRepository = staffRepository;
                this.branchRepository = branchRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtUtils = jwtUtils;
        }

        @PostMapping("register")
        public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {

                if (userRepository.existsByUsername(registerDto.getUsername())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Username is taken!"));
                }

                Role roleEnum;
                try {
                        roleEnum = Role.valueOf(registerDto.getRole().toUpperCase());
                } catch (Exception e) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Invalid role"));
                }

                // tạo user
                User user = new User();
                user.setUsername(registerDto.getUsername());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setEmail(registerDto.getEmail());
                user.setRole(roleEnum);

                Branch branch = null;

                // chỉ áp dụng cho MANAGER / EMPLOYEE
                if (roleEnum == Role.MANAGER || roleEnum == Role.EMPLOYEE) {

                        if (registerDto.getBranchId() == null) {
                                return ResponseEntity.badRequest()
                                                .body(new MessageResponse("Branch is required"));
                        }

                        branch = branchRepository.findById(registerDto.getBranchId())
                                        .orElseThrow(() -> new RuntimeException("Branch not found"));

                        user.setBranch(branch);
                }

                userRepository.save(user);

                // tạo staff nếu là nhân viên
                if (roleEnum == Role.MANAGER || roleEnum == Role.EMPLOYEE) {

                        Staff staff = new Staff();
                        staff.setUser(user);
                        staff.setBranch(branch);

                        if (roleEnum == Role.EMPLOYEE) {
                                if (registerDto.getPosition() == null) {
                                        return ResponseEntity.badRequest()
                                                        .body(new MessageResponse("Position is required for employee"));
                                }

                                staff.setPosition(
                                                StaffPosition.valueOf(registerDto.getPosition().toUpperCase()));
                        }

                        staff.setStatus(StaffStatus.ACTIVE);

                        staffRepository.save(staff);
                }

                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        }

        @PostMapping("login")
        public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                                                loginDto.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .toList();

                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Staff staff = staffRepository.findByUserId(user.getId()).orElse(null);
                return ResponseEntity.ok(new JwtResponse(
                                jwt,
                                userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getEmail(),
                                roles,
                                user.getBranch() != null ? user.getBranch().getId() : null,
                                user.getBranch() != null ? user.getBranch().getName() : null,
                                staff != null && staff.getPosition() != null
                                                ? staff.getPosition().name()
                                                : null));
        }

        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(401).body(new MessageResponse("Not authenticated"));
                }

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                // Lấy thông tin user đầy đủ từ database
                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return ResponseEntity.ok(user);
        }
}
