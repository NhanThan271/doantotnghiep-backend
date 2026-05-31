package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.restaurant.doantotnghiep.entity.enums.ScheduleStatus;

@Getter
@Setter
@Entity
@Table(name = "shift_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "work_day",
                "branch_id",
                "shift_id"
        })
})
public class ShiftSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate workDay;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private Integer requiredStaff;

    private Integer maxStaff;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
}
