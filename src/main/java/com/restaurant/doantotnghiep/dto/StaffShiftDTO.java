package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffShiftDTO {
    private Long id;
    private LocalDate workDay;
    private ShiftInfo shift;
    private StaffInfo staff;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftInfo {
        private Long id;
        private String name;
        private String startTime;
        private String endTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffInfo {
        private Long id;
        private String position;
        private UserInfo user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String fullName;
        private String username;
        private String imageUrl;
    }
}