package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.repository.BillRepository;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/revenue")
@PreAuthorize("hasRole('ADMIN')")
public class RevenueController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/overview")
    public Map<String, Object> getOverviewReport() {
        Map<String, Object> result = new HashMap<>();

        // Tổng doanh thu (đã thanh toán)
        Double totalRevenue = billRepository.getTotalRevenue();
        result.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);

        // Tổng đơn hàng
        Long totalOrders = orderRepository.count();
        result.put("totalOrders", totalOrders);

        // Tổng hóa đơn
        Long totalBills = billRepository.count();
        result.put("totalBills", totalBills);

        // Doanh thu theo ngày (nếu muốn hiển thị biểu đồ)
        List<Object[]> list = billRepository.getDailyRevenue();
        List<Map<String, Object>> dailyRevenue = new ArrayList<>();
        for (Object[] row : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row[0]);
            item.put("revenue", row[1]);
            dailyRevenue.add(item);
        }
        result.put("dailyRevenue", dailyRevenue);

        return result;
    }
}
