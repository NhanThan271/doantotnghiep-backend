package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.BranchNearestResponse;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.service.BranchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@CrossOrigin(origins = "http://localhost:3000")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Branch create(@RequestBody Branch branch) {
        return branchService.create(branch);
    }

    @GetMapping
    public List<Branch> getAll() {
        return branchService.getAll();
    }

    @GetMapping("/{id}")
    public Branch getById(@PathVariable Long id) {
        return branchService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Branch update(@PathVariable Long id, @RequestBody Branch branch) {
        return branchService.update(id, branch);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        branchService.delete(id);
    }

    @GetMapping("/nearest")
    public BranchNearestResponse getNearest(
            @RequestParam double lat,
            @RequestParam double lng) {
        return branchService.findNearest(lat, lng);
    }
}
