package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.*;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.CashierSessionService;
import com.restaurant.doantotnghiep.service.OrderService;
import com.restaurant.doantotnghiep.util.SecurityUtil;
import com.restaurant.doantotnghiep.controller.OrderWebSocketController;
import com.restaurant.doantotnghiep.dto.CashierSessionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderWebSocketController orderWebSocketController;
    private final BillRepository billRepository;
    private final TableRepository tableRepository;
    private final PromotionRepository promotionRepository;
    private final SecurityUtil securityUtil;
    private final BranchRepository branchRepository;
    private final BranchFoodRepository branchFoodRepository;
    private final RecipeRepository recipeRepository;
    private final BranchIngredientRepository branchIngredientRepository;
    private final KitchenOrderRepository kitchenOrderRepository;
    private final KitchenOrderItemRepository kitchenOrderItemRepository;
    private final FoodRepository foodRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationItemRepository reservationItemRepository;
    private final RoomRepository roomRepository;
    private final CashierSessionService cashierSessionService;
    private final StaffRepository staffRepository;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderWebSocketController orderWebSocketController,
            BillRepository billRepository,
            TableRepository tableRepository,
            PromotionRepository promotionRepository,
            SecurityUtil securityUtil,
            BranchRepository branchRepository,
            BranchFoodRepository branchFoodRepository,
            RecipeRepository recipeRepository,
            BranchIngredientRepository branchIngredientRepository,
            KitchenOrderRepository kitchenOrderRepository,
            KitchenOrderItemRepository kitchenOrderItemRepository,
            FoodRepository foodRepository,
            ReservationRepository reservationRepository,
            ReservationItemRepository reservationItemRepository,
            RoomRepository roomRepository,
            CashierSessionService cashierSessionService,
            StaffRepository staffRepository) {

        this.orderRepository = orderRepository;
        this.orderWebSocketController = orderWebSocketController;
        this.billRepository = billRepository;
        this.tableRepository = tableRepository;
        this.securityUtil = securityUtil;
        this.promotionRepository = promotionRepository;
        this.branchRepository = branchRepository;
        this.branchFoodRepository = branchFoodRepository;
        this.recipeRepository = recipeRepository;
        this.branchIngredientRepository = branchIngredientRepository;
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.kitchenOrderItemRepository = kitchenOrderItemRepository;
        this.foodRepository = foodRepository;
        this.reservationRepository = reservationRepository;
        this.reservationItemRepository = reservationItemRepository;
        this.roomRepository = roomRepository;
        this.cashierSessionService = cashierSessionService;
        this.staffRepository = staffRepository;
    }

    private BranchFood getBranchFood(Long branchId, Long foodId) {
        return branchFoodRepository.findByBranchIdAndFoodId(branchId, foodId)
                .orElseThrow(() -> new RuntimeException("Món không tồn tại ở chi nhánh"));
    }

    private BigDecimal getPriceFromBranchFood(BranchFood bf) {
        return (bf.getCustomPrice() != null && bf.getCustomPrice() > 0)
                ? BigDecimal.valueOf(bf.getCustomPrice())
                : bf.getFood().getPrice();
    }

    private BigDecimal applyPromotion(Order order) {
        if (order.getPromotion() == null)
            return order.getTotalAmount();

        Promotion promo = promotionRepository.findById(order.getPromotion().getId())
                .orElse(null);

        if (promo == null || !Boolean.TRUE.equals(promo.getIsActive())) {
            order.setPromotion(null);
            return order.getTotalAmount();
        }

        BigDecimal total = order.getTotalAmount();
        BigDecimal discount = BigDecimal.ZERO;

        if (promo.getDiscountPercentage() != null) {
            discount = total.multiply(promo.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (promo.getDiscountAmount() != null) {
            discount = promo.getDiscountAmount();
        }

        BigDecimal finalTotal = total.subtract(discount);
        return finalTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalTotal;
    }

    // Trừ kho nguyên liệu cho toàn bộ items trong order.
    private void deductIngredients(Order order) {
        Long branchId = order.getBranch().getId();

        for (OrderItem item : order.getItems()) {

            if (Boolean.TRUE.equals(item.getInventoryDeducted())) {
                continue;
            }

            Long foodId = item.getFood().getId();

            int quantity = item.getQuantity();

            List<Recipe> recipes = recipeRepository.findByFoodId(foodId);

            for (Recipe recipe : recipes) {

                Long ingredientId = recipe.getIngredient().getId();

                double totalRequired = recipe.getQuantityRequired() * quantity;

                BranchIngredient stock = branchIngredientRepository
                        .findByBranchIdAndIngredientId(
                                branchId,
                                ingredientId)
                        .orElseThrow();

                if (stock.getQuantity() < totalRequired) {
                    throw new RuntimeException(
                            "Không đủ nguyên liệu: "
                                    + recipe.getIngredient().getName());
                }

                stock.setQuantity(
                        stock.getQuantity() - totalRequired);

                branchIngredientRepository.save(stock);
            }

            // Đánh dấu sau khi xử lý hết recipe
            item.setInventoryDeducted(true);
        }

        orderRepository.save(order);
    }

    private void createKitchenOrderFor(Order order) {
        KitchenOrder kitchenOrder = KitchenOrder.builder()
                .order(order)
                .kitchenStatus(KitchenOrderStatus.WAITING)
                .build();
        kitchenOrder = kitchenOrderRepository.save(kitchenOrder);

        for (OrderItem item : order.getItems()) {
            // TẠO NHIỀU KitchenOrderItem THEO QUANTITY
            for (int i = 0; i < item.getQuantity(); i++) {
                KitchenOrderItem kitchenItem = KitchenOrderItem.builder()
                        .kitchenOrder(kitchenOrder)
                        .food(item.getFood())
                        .kitchenStatus(KitchenStatus.WAITING)
                        .build();
                kitchenOrderItemRepository.save(kitchenItem);
            }
        }
    }

    private void freeResource(Order order) {

        if (order.getTable() != null) {
            TableEntity table = tableRepository.findById(
                    order.getTable().getId())
                    .orElseThrow();

            table.setStatus(Status.FREE);

            tableRepository.save(table);
        }

        if (order.getRoom() != null) {
            Room room = roomRepository.findById(
                    order.getRoom().getId())
                    .orElseThrow();

            room.setStatus(RoomStatus.ACTIVE);

            roomRepository.save(room);
        }
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {

        User user = securityUtil.getCurrentUser();
        if (user != null)
            order.setEmployee(user);

        if (order.getCustomerName() == null || order.getCustomerName().isBlank()) {
            order.setCustomerName("Khách lẻ");
        }

        // Xác định chi nhánh
        if (order.getBranch() == null || order.getBranch().getId() == null) {
            order.setBranch(user.getBranch());
        } else {
            order.setBranch(branchRepository.findById(order.getBranch().getId())
                    .orElseThrow(() -> new RuntimeException("Branch not found")));
        }

        // Gán bàn và đánh dấu bận
        if (order.getTable() != null && order.getTable().getId() != null) {
            TableEntity table = tableRepository.findById(order.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            if (table.getType() == TableType.PHYSICAL) {
                table.setStatus(Status.OCCUPIED);
                tableRepository.save(table);
            }
            order.setTable(table);
        }

        if (order.getRoom() != null && order.getRoom().getId() != null) {

            Room room = roomRepository.findById(order.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            room.setStatus(RoomStatus.OCCUPIED);

            roomRepository.save(room);

            order.setRoom(room);
        }

        // Xử lý items: gán BranchFood, price, tính subtotal
        List<OrderItem> items = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                BranchFood bf = getBranchFood(order.getBranch().getId(), item.getFood().getId());
                item.setFood(bf.getFood());
                item.setBranchFood(bf);
                item.setOrder(order);
                item.setPrice(
                        item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0
                                ? item.getPrice()
                                : getPriceFromBranchFood(bf));
                item.calculateSubtotal();
                items.add(item);
            }
        }
        order.setItems(items);

        calculateOrderTotal(order);

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // Lưu order
        Order saved = orderRepository.save(order);

        createKitchenOrderFor(saved);

        orderWebSocketController.sendNewOrder(saved);
        return saved;
    }

    private void calculateOrderTotal(Order order) {
        order.recalcTotal();
        BigDecimal total = order.getTotalAmount();

        if (order.getRoom() != null && order.getRoom().getId() != null) {
            roomRepository.findById(order.getRoom().getId()).ifPresent(room -> {
                if (room.getRoomFee() != null) {
                    order.setTotalAmount(total.add(room.getRoomFee()));
                }
            });
        }

        BigDecimal afterPromo = applyPromotion(order);
        order.setTotalAmount(afterPromo);
    }

    @Override
    @Transactional
    public Order addFoodToOrder(Long orderId, Food food, int quantity) {

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BranchFood bf = getBranchFood(order.getBranch().getId(), food.getId());

        OrderItem newItem = new OrderItem();
        newItem.setOrder(order);
        newItem.setFood(bf.getFood());
        newItem.setBranchFood(bf);
        newItem.setQuantity(quantity);
        newItem.setPrice(getPriceFromBranchFood(bf));
        newItem.calculateSubtotal();
        order.getItems().add(newItem);

        calculateOrderTotal(order);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order addMultipleFoodsToOrder(Long orderId, List<Map<String, Object>> items) {

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Long branchId = order.getBranch().getId();

        for (Map<String, Object> i : items) {
            Long foodId = ((Number) i.get("foodId")).longValue();
            Integer quantity = (Integer) i.get("quantity");

            BranchFood bf = getBranchFood(branchId, foodId);

            // ← LUÔN tạo mới, không cộng dồn
            OrderItem newItem = new OrderItem();
            newItem.setOrder(order);
            newItem.setFood(bf.getFood());
            newItem.setBranchFood(bf);
            newItem.setQuantity(quantity);
            newItem.setPrice(getPriceFromBranchFood(bf));
            newItem.calculateSubtotal();
            order.getItems().add(newItem);
        }
        calculateOrderTotal(order);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderStatus status, PaymentMethod method) {

        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        calculateOrderTotal(order);

        if (status == OrderStatus.PAID) {
            BigDecimal finalAmount = order.getTotalAmount();

            if (order.getReservation() != null) {
                finalAmount = finalAmount.subtract(
                        BigDecimal.valueOf(
                                order.getReservation().getDepositAmount()));
                if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    finalAmount = BigDecimal.ZERO;
                }
            }

            if (!billRepository.existsByOrderId(order.getId())) {
                Bill bill = Bill.builder()
                        .order(order)
                        .totalAmount(finalAmount)
                        .paymentMethod(method)
                        .paymentStatus(PaymentStatus.PAID)
                        .issuedAt(LocalDateTime.now())
                        .build();
                billRepository.save(bill);
            }

            if (order.getReservation() != null) {
                Reservation reservation = order.getReservation();
                reservation.setPaymentStatus(PaymentStatus.PAID);
                reservationRepository.save(reservation);
            }
            freeResource(order);
        }

        if (status == OrderStatus.CANCELED) {
            freeResource(order);
        }

        Order updated = orderRepository.save(order);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {

        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == OrderStatus.COMPLETED && oldStatus != OrderStatus.COMPLETED) {

        }

        Order updated = orderRepository.save(order);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        orderWebSocketController.sendOrderDeleted(id);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchOrders(keyword.toLowerCase());
    }

    @Transactional
    public void addKitchenItemsForNewFood(Long orderId, Long foodId, int quantity) {
        List<KitchenOrder> kitchenOrders = kitchenOrderRepository.findByOrderId(orderId);
        KitchenOrder targetKitchenOrder;

        if (kitchenOrders.isEmpty()) {
            Order order = orderRepository.findById(orderId).orElseThrow();
            targetKitchenOrder = KitchenOrder.builder()
                    .order(order)
                    .kitchenStatus(KitchenOrderStatus.WAITING)
                    .build();
            targetKitchenOrder = kitchenOrderRepository.save(targetKitchenOrder);
        } else {
            targetKitchenOrder = kitchenOrders.get(kitchenOrders.size() - 1);
            targetKitchenOrder.setKitchenStatus(KitchenOrderStatus.WAITING);
            targetKitchenOrder = kitchenOrderRepository.save(targetKitchenOrder);
        }

        Food food = foodRepository.findById(foodId).orElseThrow();
        Order order = orderRepository.findById(orderId).orElseThrow();

        // TẠO NHIỀU KitchenOrderItem THEO QUANTITY
        for (int i = 0; i < quantity; i++) {
            KitchenOrderItem kitchenItem = KitchenOrderItem.builder()
                    .kitchenOrder(targetKitchenOrder)
                    .food(food)
                    .kitchenStatus(KitchenStatus.WAITING)
                    .build();
            kitchenOrderItemRepository.save(kitchenItem);
        }
    }

    @Override
    @Transactional
    public Order createOrderFromReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Order order = new Order();

        order.setReservation(reservation);

        order.setBranch(reservation.getBranch());

        order.setTable(reservation.getTable());

        order.setRoom(reservation.getRoom());

        order.setCustomerName(reservation.getCustomerName());

        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItem> orderItems = new ArrayList<>();

        List<ReservationItem> reservationItems = reservationItemRepository.findByReservationId(reservationId);

        for (ReservationItem ri : reservationItems) {

            OrderItem oi = new OrderItem();

            oi.setOrder(order);

            oi.setFood(ri.getBranchFood().getFood());

            oi.setBranchFood(ri.getBranchFood());

            oi.setQuantity(ri.getQuantity());

            oi.setPrice(ri.getPrice());

            oi.calculateSubtotal();

            orderItems.add(oi);
        }

        order.setItems(orderItems);

        calculateOrderTotal(order);

        Order savedOrder = orderRepository.save(order);

        createKitchenOrderFor(savedOrder);
        orderWebSocketController.sendNewOrder(savedOrder);

        return savedOrder;
    }

    @Override
    @Transactional
    public Order addFoodToOrderWithPrice(Long orderId, Food food, int quantity, Double customPrice) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BranchFood bf = getBranchFood(order.getBranch().getId(), food.getId());

        OrderItem newItem = new OrderItem();
        newItem.setOrder(order);
        newItem.setFood(bf.getFood());
        newItem.setBranchFood(bf);
        newItem.setQuantity(quantity);
        // ✅ DÙNG PRICE TỪ REQUEST (customPrice)
        newItem.setPrice(BigDecimal.valueOf(customPrice));
        newItem.calculateSubtotal();
        order.getItems().add(newItem);

        calculateOrderTotal(order);

        return orderRepository.save(order);
    }
}