package com.restaurant.doantotnghiep.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.restaurant.doantotnghiep.dto.FoodForecastDTO;
import com.restaurant.doantotnghiep.repository.OrderItemRepository;
import com.restaurant.doantotnghiep.service.FoodForecastService;

import lombok.*;

@Service
@RequiredArgsConstructor
public class FoodForecastServiceImpl implements FoodForecastService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public List<FoodForecastDTO> getForecast(String mode, Long branchId, int topN) {
        boolean isWeekly = "WEEK".equalsIgnoreCase(mode);
        LocalDateTime from = isWeekly
                ? LocalDateTime.now().minusWeeks(12)
                : LocalDateTime.now().minusMonths(6);
        LocalDateTime to = LocalDateTime.now();

        // Dùng 0L thay null để tránh lỗi IS NULL trên PostgreSQL
        Long safeBranchId = (branchId != null) ? branchId : 0L;

        List<Object[]> rows = isWeekly
                ? orderItemRepository.findWeeklySalesByFood(from, to, safeBranchId)
                : orderItemRepository.findMonthlySalesByFood(from, to, safeBranchId);

        Map<Long, FoodForecastDTO> map = new LinkedHashMap<>();

        for (Object[] row : rows) {
            if (row[0] == null)
                continue;
            Long foodId = ((Number) row[0]).longValue();
            String name = row[1] != null ? row[1].toString() : "";
            Long qty = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            map.computeIfAbsent(foodId, id -> {
                FoodForecastDTO dto = new FoodForecastDTO();
                dto.setFoodId(id);
                dto.setFoodName(name);
                dto.setHistory(new ArrayList<>());
                dto.setTotalPast(0L);
                return dto;
            });

            FoodForecastDTO dto = map.get(foodId);
            dto.getHistory().add(qty);
            dto.setTotalPast(dto.getTotalPast() + qty);
        }

        map.values().forEach(dto -> {
            List<Long> hist = dto.getHistory();
            double avg = hist.stream().mapToLong(Long::longValue).average().orElse(0);
            dto.setAvgPerPeriod(avg);
            dto.setForecastNextPeriod(weightedForecast(hist));
            dto.setTrend(calcTrend(hist));
        });

        return map.values().stream()
                .sorted(Comparator.comparingLong(FoodForecastDTO::getTotalPast).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    // Weighted moving average: kỳ cuối weight cao nhất
    private Long weightedForecast(List<Long> hist) {
        if (hist.isEmpty())
            return 0L;
        int n = hist.size();
        double weightSum = 0, valueSum = 0;
        for (int i = 0; i < n; i++) {
            double w = i + 1; // weight tăng dần
            weightSum += w;
            valueSum += w * hist.get(i);
        }
        return Math.round(valueSum / weightSum);
    }

    // So sánh trung bình nửa đầu và nửa sau lịch sử
    private String calcTrend(List<Long> hist) {
        if (hist.size() < 2)
            return "STABLE";
        int mid = hist.size() / 2;
        double first = hist.subList(0, mid).stream().mapToLong(Long::longValue).average().orElse(0);
        double second = hist.subList(mid, hist.size()).stream().mapToLong(Long::longValue).average().orElse(0);
        if (second > first * 1.1)
            return "UP";
        if (second < first * 0.9)
            return "DOWN";
        return "STABLE";
    }
}