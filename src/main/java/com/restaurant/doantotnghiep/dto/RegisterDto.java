package com.restaurant.doantotnghiep.dto;

import lombok.*;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String fullName;
    private Long branchId;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String position;
}
