package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.OrderStatusDTO;
import com.restaurant.doantotnghiep.entity.Bill;
import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentMethod;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.Status;
import com.restaurant.doantotnghiep.mapper.OrderMapper;
import com.restaurant.doantotnghiep.repository.BillRepository;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.service.OrderService;
import com.restaurant.doantotnghiep.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final FoodService foodService;
    private final OrderWebSocketController orderWebSocketController;
    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private final TableRepository tableRepository;

    @Autowired
    public OrderController(OrderService orderService,
            FoodService foodService,
            OrderWebSocketController orderWebSocketController,
            OrderRepository orderRepository,
            BillRepository billRepository,
            TableRepository tableRepository) {
        this.orderService = orderService;
        this.foodService = foodService;
        this.orderWebSocketController = orderWebSocketController;
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.tableRepository = tableRepository;
    }

    // Lấy danh sách tất cả đơn hàng
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithBranch();
    }

    // Xem chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    @Transactional
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + id));
        return ResponseEntity.ok(OrderMapper.toStatusDTO(order));
    }

    // Tạo đơn hàng mới → realtime gửi cho nhân viên (barista)
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        System.out.println("Received order: " + order);
        Order savedOrder = orderService.createOrder(order);
        orderWebSocketController.sendNewOrder(savedOrder);
        return savedOrder;
    }

    // Thêm món vào đơn hàng
    @PostMapping("/{orderId}/add-food")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public Order addFoodToOrder(@PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        Long foodId = ((Number) body.get("foodId")).longValue();
        Integer quantity = (Integer) body.get("quantity");

        Food food = foodService.getFoodById(foodId);
        Order updatedOrder = orderService.addFoodToOrder(orderId, food, quantity);

        orderWebSocketController.sendOrderUpdate(updatedOrder); // realtime update
        return updatedOrder;
    }

    // Thêm món vào đơn hàng đã tồn tại
    @PostMapping("/{orderId}/add-items")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<?> addItemsToExistingOrder(
            @PathVariable Long orderId,
            @RequestBody List<Map<String, Object>> newItems) {

        try {
            System.out.println("Nhận yêu cầu thêm món vào đơn #" + orderId);
            System.out.println("Items: " + newItems);

            Order existingOrder = orderService.getOrderById(orderId);

            if (existingOrder == null) {
                return ResponseEntity.status(404).body("Không tìm thấy đơn hàng #" + orderId);
            }

            System.out.println("Đơn hiện tại: status=" + existingOrder.getStatus());

            // Không cho phép thêm món vào đơn đã thanh toán hoặc đã hủy
            if (existingOrder.getStatus() == OrderStatus.PAID) {
                return ResponseEntity.badRequest().body("Không thể thêm món vào đơn đã thanh toán");
            }

            if (existingOrder.getStatus() == OrderStatus.CANCELED) {
                return ResponseEntity.badRequest().body("Không thể thêm món vào đơn đã hủy");
            }

            // Thêm từng món vào đơn
            for (Map<String, Object> item : newItems) {
                Long foodId = ((Number) item.get("foodId")).longValue();
                Integer quantity = (Integer) item.get("quantity");

                System.out.println("Thêm món #" + foodId + " x" + quantity);

                Food food = foodService.getFoodById(foodId);
                existingOrder = orderService.addFoodToOrder(orderId, food, quantity);
            }

            // Nếu đơn đã completed, chuyển về preparing
            if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
                System.out.println("Đơn đã hoàn thành, chuyển về PREPARING");
                existingOrder = orderService.updateOrderStatus(orderId, OrderStatus.PREPARING);
            }

            System.out.println("Thêm món thành công! Total: " + existingOrder.getTotalAmount());

            // Gửi update qua WebSocket
            orderWebSocketController.sendOrderUpdate(existingOrder);

            return ResponseEntity.ok(existingOrder);

        } catch (Exception e) {
            System.err.println("Lỗi khi thêm món: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    // Cập nhật thông tin đơn hàng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE','CUSTOMER')")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") OrderStatus status,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") PaymentMethod paymentMethod) {

        return orderService.updateOrder(id, status, paymentMethod);
    }

    // Xóa đơn hàng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        orderWebSocketController.sendOrderDeleted(id); // realtime delete
    }

    // Tìm kiếm đơn hàng theo từ khóa
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public List<Order> searchOrders(@RequestParam("keyword") String keyword) {
        return orderService.searchOrders(keyword);
    }

    // Các hành động cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public Order confirmOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.CONFIRMED);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @PutMapping("/{id}/prepare")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    @Transactional(readOnly = false)
    public ResponseEntity<?> prepareOrder(@PathVariable Long id) {
        try {
            System.out.println("Request to prepare order #" + id);

            // LẤY ORDER VỚI EAGER LOADING
            Order updated = orderRepository.findWithItemsById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + id));

            // FORCE LOAD TẤT CẢ RELATIONSHIPS
            if (updated.getItems() != null) {
                updated.getItems().forEach(item -> {
                    if (item.getFood() != null) {
                        item.getFood().getName(); // force load
                    }
                    if (item.getBranchFood() != null) {
                        item.getBranchFood().getId(); // force load
                    }
                });
            }

            if (updated.getTable() != null) {
                updated.getTable().getNumber();
            }

            if (updated.getPromotion() != null) {
                updated.getPromotion().getName();
            }

            // CẬP NHẬT TRẠNG THÁI
            updated.setStatus(OrderStatus.PREPARING);
            updated.setUpdatedAt(LocalDateTime.now());

            // LƯU VÀO DB
            Order saved = orderRepository.save(updated);

            // FORCE LOAD LẠI SAU KHI SAVE
            saved.getItems().size();

            // CONVERT TO DTO
            OrderStatusDTO dto = OrderMapper.toStatusDTO(saved);

            System.out.println("DTO created: " + dto);

            // GỬI WEBSOCKET
            try {
                orderWebSocketController.sendOrderUpdate(saved);
            } catch (Exception e) {
                System.err.println("WebSocket error (non-critical): " + e.getMessage());
            }

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            System.err.println("Error preparing order #" + id);
            e.printStackTrace(); // IN RA STACK TRACE ĐẦY ĐỦ

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", e.getClass().getName(),
                            "message", e.getMessage() != null ? e.getMessage() : "Unknown error",
                            "orderId", id));
        }
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        try {
            System.out.println("Request to complete order #" + id);

            Order updated = orderService.updateOrderStatus(id, OrderStatus.COMPLETED);

            // SỬ DỤNG DTO THAY VÌ RETURN ENTITY TRỰC TIẾP
            OrderStatusDTO dto = OrderMapper.toStatusDTO(updated);

            System.out.println("Order completed successfully: #" + id);

            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            System.err.println("Error completing order #" + id + ": " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "ORDER_COMPLETION_FAILED",
                            "message", e.getMessage(),
                            "orderId", id));

        } catch (Exception e) {
            System.err.println("Unexpected error completing order #" + id);
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "INTERNAL_ERROR",
                            "message", "Lỗi hệ thống khi hoàn thành đơn hàng",
                            "orderId", id));
        }
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    @Transactional
    public ResponseEntity<?> customerPayOrder(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {

        try {
            System.out.println("Payment request for order #" + id);
            System.out.println("Payment method: " + paymentMethod);

            // VALIDATE PAYMENT METHOD
            PaymentMethod method;
            try {
                method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Phương thức thanh toán không hợp lệ: " + paymentMethod));
            }

            // LẤY ORDER VỚI EAGER LOADING
            Order order = orderRepository.findWithItemsById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + id));

            System.out.println("Order found: #" + order.getId() + " - Status: " + order.getStatus());

            // KIỂM TRA TRẠNG THÁI
            if (order.getStatus() == OrderStatus.PAID) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Đơn hàng đã được thanh toán!"));
            }

            if (order.getStatus() == OrderStatus.CANCELED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Không thể thanh toán đơn đã hủy!"));
            }

            // CẬP NHẬT TRẠNG THÁI
            LocalDateTime now = LocalDateTime.now();
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(now);
            order.setUpdatedAt(now);

            // TẠO BILL NẾU CHƯA CÓ
            if (!billRepository.existsByOrderId(order.getId())) {
                Bill bill = Bill.builder()
                        .order(order)
                        .totalAmount(order.getTotalAmount())
                        .paymentMethod(method)
                        .paymentStatus(PaymentStatus.PAID)
                        .issuedAt(now)
                        .notes("Thanh toán qua " + method.name())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

                billRepository.save(bill);
                System.out.println("Bill created for order #" + id);
            }

            // GIẢI PHÓNG BÀN
            if (order.getTable() != null) {
                TableEntity table = order.getTable();
                table.setStatus(Status.FREE);
                table.setUpdatedAt(now);
                tableRepository.save(table);
                System.out.println("Table #" + table.getNumber() + " freed");
            }

            // LƯU ORDER
            Order savedOrder = orderRepository.save(order);

            // FORCE LOAD
            savedOrder.getItems().size();
            if (savedOrder.getTable() != null)
                savedOrder.getTable().getNumber();
            if (savedOrder.getPromotion() != null)
                savedOrder.getPromotion().getName();

            // GỬI WEBSOCKET
            try {
                orderWebSocketController.sendOrderUpdate(savedOrder);
            } catch (Exception e) {
                System.err.println("WebSocket error: " + e.getMessage());
            }

            // TRẢ VỀ RESPONSE
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thanh toán thành công!");
            response.put("orderId", savedOrder.getId());
            response.put("status", savedOrder.getStatus().name());
            response.put("paymentMethod", method.name());
            response.put("totalAmount", savedOrder.getTotalAmount());

            System.out.println("Payment completed for order #" + id);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid payment method: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phương thức thanh toán không hợp lệ!"));

        } catch (Exception e) {
            System.err.println("Payment error for order #" + id);
            e.printStackTrace(); // IN RA STACK TRACE ĐẦY ĐỦ

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", e.getClass().getName(),
                            "message", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định",
                            "orderId", id));
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public Order cancelOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.CANCELED);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getPendingOrders() {
        return orderService.getAllOrders()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .collect(Collectors.toList());
    }

}
