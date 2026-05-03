package com.restaurant.doantotnghiep.dto;

import com.restaurant.doantotnghiep.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchNearestResponse {

    private Branch branch;
    private double distance;
    private String distanceText;
}
