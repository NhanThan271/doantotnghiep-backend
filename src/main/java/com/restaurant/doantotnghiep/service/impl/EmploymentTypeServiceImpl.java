package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.EmploymentType;
import com.restaurant.doantotnghiep.repository.EmploymentTypeRepository;
import com.restaurant.doantotnghiep.service.EmploymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentTypeServiceImpl implements EmploymentTypeService {

    private final EmploymentTypeRepository repository;

    @Override
    public List<EmploymentType> getAll() {
        return repository.findAll();
    }

    @Override
    public EmploymentType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EmploymentType not found"));
    }

    @Override
    public EmploymentType create(EmploymentType employmentType) {
        return repository.save(employmentType);
    }

    @Override
    public EmploymentType update(Long id, EmploymentType employmentType) {

        EmploymentType existing = getById(id);

        existing.setName(employmentType.getName());
        existing.setMaxShiftPerMonth(employmentType.getMaxShiftPerMonth());
        existing.setSalaryCoefficient(employmentType.getSalaryCoefficient());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}