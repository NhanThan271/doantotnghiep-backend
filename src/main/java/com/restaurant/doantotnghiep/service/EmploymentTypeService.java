package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.EmploymentType;

import java.util.List;

public interface EmploymentTypeService {

    List<EmploymentType> getAll();

    EmploymentType getById(Long id);

    EmploymentType create(EmploymentType employmentType);

    EmploymentType update(Long id, EmploymentType employmentType);

    void delete(Long id);
}