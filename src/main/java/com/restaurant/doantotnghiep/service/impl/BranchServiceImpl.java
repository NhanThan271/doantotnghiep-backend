package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public Branch create(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    @Override
    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    @Override
    public Branch update(Long id, Branch branch) {
        Branch existing = getById(id);
        existing.setName(branch.getName());
        existing.setAddress(branch.getAddress());
        existing.setPhone(branch.getPhone());
        existing.setIsActive(branch.getIsActive());
        return branchRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Branch branch = getById(id);
        branch.setIsActive(false);
        branchRepository.deleteById(id);
    }
}
