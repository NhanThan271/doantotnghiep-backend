package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.doantotnghiep.entity.enums.AttendanceStatus;

@Getter
@Setter
@Entity
@Table(name = "attendances", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "staff_id", "shift_schedule_id" })
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnore
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "shift_schedule_id", nullable = false)
    @JsonIgnore
    private ShiftSchedule shiftSchedule;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
}