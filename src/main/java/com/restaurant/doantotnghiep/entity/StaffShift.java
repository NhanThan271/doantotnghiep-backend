package com.restaurant.doantotnghiep.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "staff_shifts")
public class StaffShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "shifts" })
    private Staff staff;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "staffShifts" })
    private Shift shift;

    @Column(nullable = false)
    private LocalDate workDay;
}
