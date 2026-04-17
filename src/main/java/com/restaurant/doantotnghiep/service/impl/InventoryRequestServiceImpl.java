package com.restaurant.doantotnghiep.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.doantotnghiep.entity.BranchIngredient;
import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.InventoryRequestItem;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.WarehouseExport;
import com.restaurant.doantotnghiep.entity.WarehouseExportItem;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;
import com.restaurant.doantotnghiep.entity.enums.RequestStatus;
import com.restaurant.doantotnghiep.repository.BranchIngredientRepository;
import com.restaurant.doantotnghiep.repository.InventoryRequestItemRepository;
import com.restaurant.doantotnghiep.repository.InventoryRequestRepository;
import com.restaurant.doantotnghiep.repository.WarehouseExportItemRepository;
import com.restaurant.doantotnghiep.repository.WarehouseExportRepository;
import com.restaurant.doantotnghiep.repository.WarehouseInventoryRepository;
import com.restaurant.doantotnghiep.service.InventoryRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestServiceImpl implements InventoryRequestService {

    private final InventoryRequestRepository repository;
    private final InventoryRequestItemRepository itemRepo;
    private final BranchIngredientRepository branchIngredientRepo;
    private final WarehouseInventoryRepository warehouseInventoryRepo;
    private final WarehouseExportRepository exportRepo;
    private final WarehouseExportItemRepository exportItemRepo;

    @Override
    public InventoryRequest create(InventoryRequest request, User requester) {
        request.setRequestedBy(requester);
        request.setStatus(RequestStatus.PENDING);
        return repository.save(request);
    }

    @Override
    @Transactional
    public InventoryRequest approve(Long id, User approver) {
        InventoryRequest req = getById(id);

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be approved");
        }

        if (req.getWarehouse() == null) {
            throw new RuntimeException("Request chưa chọn kho");
        }

        // Lấy danh sách nguyên liệu yêu cầu
        List<InventoryRequestItem> items = itemRepo.findByRequestId(req.getId());

        if (items.isEmpty()) {
            throw new RuntimeException("Request không có nguyên liệu");
        }

        Long warehouseId = req.getWarehouse().getId();

        // Check tồn kho
        for (InventoryRequestItem item : items) {
            WarehouseInventory wi = warehouseInventoryRepo
                    .findByWarehouseIdAndIngredientId(warehouseId, item.getIngredient().getId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not in warehouse"));

            if (wi.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Không đủ kho: " + item.getIngredient().getName());
            }
        }

        // tạo phiếu xuất kho
        WarehouseExport export = WarehouseExport.builder()
                .warehouse(req.getWarehouse())
                .branch(req.getBranch())
                .request(req)
                .createdBy(approver)
                .build();

        export = exportRepo.save(export);

        // xử lý xuất kho
        for (InventoryRequestItem item : items) {

            WarehouseInventory wi = warehouseInventoryRepo
                    .findByWarehouseIdAndIngredientId(warehouseId, item.getIngredient().getId())
                    .get();

            wi.setQuantity(wi.getQuantity() - item.getQuantity());
            warehouseInventoryRepo.save(wi);

            BranchIngredient bi = branchIngredientRepo
                    .findByBranchIdAndIngredientId(
                            req.getBranch().getId(),
                            item.getIngredient().getId())
                    .orElse(
                            BranchIngredient.builder()
                                    .branch(req.getBranch())
                                    .ingredient(item.getIngredient())
                                    .quantity(0.0)
                                    .build());

            bi.setQuantity(bi.getQuantity() + item.getQuantity());
            branchIngredientRepo.save(bi);

            WarehouseExportItem exportItem = WarehouseExportItem.builder()
                    .export(export)
                    .ingredient(item.getIngredient())
                    .quantity(item.getQuantity())
                    .build();

            exportItemRepo.save(exportItem);
        }

        // cập nhật trạng thái request
        req.setStatus(RequestStatus.APPROVED);
        req.setApprovedBy(approver);
        req.setApprovedAt(LocalDateTime.now());

        return repository.save(req);
    }

    @Override
    public InventoryRequest reject(Long id, String note, User approver) {
        InventoryRequest req = getById(id);

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be rejected");
        }

        req.setStatus(RequestStatus.REJECTED);
        req.setNote(note);
        req.setApprovedBy(approver);
        req.setApprovedAt(LocalDateTime.now());

        return repository.save(req);
    }

    @Override
    public List<InventoryRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public List<InventoryRequest> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    public InventoryRequest getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
    }
}
