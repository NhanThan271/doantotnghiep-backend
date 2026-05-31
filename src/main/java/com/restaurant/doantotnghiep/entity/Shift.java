package com.restaurant.doantotnghiep.entity;

import java.time.LocalTime;

import com.restaurant.doantotnghiep.entity.enums.ShiftStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer workingHours;

    private Double shiftAllowance;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private ShiftStatus status;
}
