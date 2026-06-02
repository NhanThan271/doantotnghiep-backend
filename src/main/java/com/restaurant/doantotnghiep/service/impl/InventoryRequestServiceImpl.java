package com.restaurant.doantotnghiep.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.doantotnghiep.dto.InventoryRequestCreateDTO;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.BranchIngredient;
import com.restaurant.doantotnghiep.entity.Ingredient;
import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.InventoryRequestItem;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.Warehouse;
import com.restaurant.doantotnghiep.entity.WarehouseExport;
import com.restaurant.doantotnghiep.entity.WarehouseExportItem;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;
import com.restaurant.doantotnghiep.entity.enums.RequestStatus;
import com.restaurant.doantotnghiep.entity.enums.RequestType;
import com.restaurant.doantotnghiep.repository.BranchIngredientRepository;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.IngredientRepository;
import com.restaurant.doantotnghiep.repository.InventoryBatchRepository;
import com.restaurant.doantotnghiep.repository.InventoryRequestItemRepository;
import com.restaurant.doantotnghiep.repository.InventoryRequestRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.repository.WarehouseExportItemRepository;
import com.restaurant.doantotnghiep.repository.WarehouseExportRepository;
import com.restaurant.doantotnghiep.repository.WarehouseInventoryRepository;
import com.restaurant.doantotnghiep.repository.WarehouseRepository;
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
    private final BranchRepository branchRepo;
    private final WarehouseRepository warehouseRepo;
    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepository;
    private final InventoryBatchRepository inventoryBatchRepository;

    @Override
    @Transactional
    public InventoryRequest create(InventoryRequestCreateDTO dto, User requester) {
        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Warehouse warehouse = warehouseRepo.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        User fullRequester = userRepository.findById(requester.getId())
                .orElse(requester);

        InventoryRequest request = InventoryRequest.builder()
                .branch(branch)
                .warehouse(warehouse)
                .type(RequestType.valueOf(dto.getType()))
                .reason(dto.getReason())
                .status(RequestStatus.PENDING)
                .requestedBy(fullRequester)
                .build();

        request = repository.save(request);

        for (InventoryRequestCreateDTO.ItemDTO itemDto : dto.getItems()) {
            Ingredient ingredient = ingredientRepo.findById(itemDto.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            InventoryRequestItem item = InventoryRequestItem.builder()
                    .request(request)
                    .ingredient(ingredient)
                    .quantity(itemDto.getQuantity())
                    .build();

            itemRepo.save(item);
        }

        return request;
    }

    @Override
    @Transactional
    public InventoryRequest approve(Long id, User approver) {
        InventoryRequest req = getById(id);
        User fullApprover = userRepository.findById(approver.getId()).orElse(approver);

        if (req.getStatus() != RequestStatus.PENDING)
            throw new RuntimeException("Only PENDING requests can be approved");
        if (req.getWarehouse() == null)
            throw new RuntimeException("Request chưa chọn kho");

        List<InventoryRequestItem> items = itemRepo.findByRequestId(req.getId());
        if (items.isEmpty())
            throw new RuntimeException("Request không có nguyên liệu");

        Long warehouseId = req.getWarehouse().getId();

        LocalDate minExpiryDate = LocalDate.now().plusDays(5);

        for (InventoryRequestItem item : items) {
            List<InventoryBatch> validBatches = inventoryBatchRepository
                    .findByWarehouseIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                            warehouseId,
                            item.getIngredient().getId(),
                            0.0,
                            minExpiryDate);

            double availableQty = validBatches.stream()
                    .mapToDouble(InventoryBatch::getRemainingQuantity)
                    .sum();

            if (availableQty < item.getQuantity()) {
                throw new RuntimeException(
                        "Không đủ hàng có thể xuất cho: " + item.getIngredient().getName()
                                + " (cần " + item.getQuantity()
                                + ", khả dụng " + availableQty
                                + " — đã loại trừ các lô hết hạn hoặc còn ≤ 5 ngày HSD)");
            }
        }

        WarehouseExport export = WarehouseExport.builder()
                .warehouse(req.getWarehouse())
                .branch(req.getBranch())
                .request(req)
                .createdBy(fullApprover)
                .build();
        export = exportRepo.save(export);

        for (InventoryRequestItem item : items) {
            double remain = item.getQuantity();

            List<InventoryBatch> batches = inventoryBatchRepository
                    .findByWarehouseIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                            warehouseId,
                            item.getIngredient().getId(),
                            0.0,
                            minExpiryDate);

            for (InventoryBatch batch : batches) {
                if (remain <= 0)
                    break;

                double taken = Math.min(batch.getRemainingQuantity(), remain);
                batch.setRemainingQuantity(batch.getRemainingQuantity() - taken);
                inventoryBatchRepository.save(batch);

                exportItemRepo.save(WarehouseExportItem.builder()
                        .export(export)
                        .ingredient(item.getIngredient())
                        .batch(batch)
                        .quantity(taken)
                        .build());

                remain -= taken;
            }

            if (remain > 0)
                throw new RuntimeException("Không đủ batch hợp lệ cho: " + item.getIngredient().getName());

            // Trừ tồn kho tổng hợp
            WarehouseInventory wi = warehouseInventoryRepo
                    .findByWarehouseIdAndIngredientId(warehouseId, item.getIngredient().getId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not in warehouse"));
            wi.setQuantity(wi.getQuantity() - item.getQuantity());
            warehouseInventoryRepo.save(wi);
        }

        req.setStatus(RequestStatus.APPROVED);
        req.setApprovedBy(fullApprover);
        req.setApprovedAt(LocalDateTime.now());
        return repository.save(req);
    }

    @Override
    @Transactional
    public InventoryRequest confirmReceived(Long id, User manager) {
        User fullManager = userRepository.findById(manager.getId()).orElse(manager);
        InventoryRequest req = getById(id);

        if (req.getStatus() != RequestStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED requests can be confirmed as received");
        }

        WarehouseExport export = exportRepo.findByRequestId(req.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất"));

        List<WarehouseExportItem> exportItems = exportItemRepo.findByExportId(export.getId());

        Map<Long, Double> ingredientQtyMap = new HashMap<>();
        Map<Long, InventoryBatch> ingredientBestBatch = new HashMap<>(); 

        for (WarehouseExportItem item : exportItems) {
            Long ingId = item.getIngredient().getId();
            ingredientQtyMap.merge(ingId, item.getQuantity(), Double::sum);
            ingredientBestBatch.merge(ingId, item.getBatch(), (existing,
                    newBatch) -> existing.getExpiryDate().isBefore(newBatch.getExpiryDate()) ? existing : newBatch);
        }

        for (Map.Entry<Long, Double> entry : ingredientQtyMap.entrySet()) {
            Long ingId = entry.getKey();
            double totalQty = entry.getValue();
            Ingredient ingredient = ingredientRepo.findById(ingId)
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            BranchIngredient bi = branchIngredientRepo
                    .findByBranchIdAndIngredientId(req.getBranch().getId(), ingId)
                    .orElse(BranchIngredient.builder()
                            .branch(req.getBranch())
                            .ingredient(ingredient)
                            .quantity(0.0)
                            .build());

            bi.setQuantity(bi.getQuantity() + totalQty);
            branchIngredientRepo.save(bi);

            InventoryBatch batch = InventoryBatch.builder()
                    .branch(req.getBranch())
                    .warehouse(null)
                    .ingredient(ingredient)
                    .quantity(totalQty)
                    .remainingQuantity(totalQty)
                    .expiryDate(ingredientBestBatch.get(ingId).getExpiryDate())
                    .importedAt(LocalDateTime.now())
                    .build();

            inventoryBatchRepository.save(batch);
        }

        req.setStatus(RequestStatus.RECEIVED);
        req.setReceivedBy(fullManager);
        req.setReceivedAt(LocalDateTime.now());

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
