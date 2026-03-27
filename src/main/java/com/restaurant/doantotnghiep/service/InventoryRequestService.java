package com.restaurant.doantotnghiep.service;

import java.util.List;

import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.User;

public interface InventoryRequestService {

    InventoryRequest create(InventoryRequest request, User requester);

    InventoryRequest approve(Long id, User approver);

    InventoryRequest reject(Long id, String note, User approver);

    List<InventoryRequest> getAll();

    List<InventoryRequest> getByBranch(Long branchId);

    InventoryRequest getById(Long id);
}
