package com.restaurant.doantotnghiep.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
        LocalDateTime to;
        LocalDateTime from;

        if (isWeekly) {
            LocalDateTime startOfThisWeek = LocalDateTime.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .toLocalDate().atStartOfDay();
            to = startOfThisWeek.minusSeconds(1);
            from = to.minusWeeks(12);
        } else {
            LocalDateTime startOfThisMonth = LocalDateTime.now()
                    .withDayOfMonth(1).toLocalDate().atStartOfDay();
            to = startOfThisMonth.minusSeconds(1);
            from = to.minusMonths(6);
        }
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

    // So sánh kỳ trước với kỳ gần nhất
    private String calcTrend(List<Long> hist) {
        if (hist.size() < 2)
            return "STABLE";

        long prev = hist.get(hist.size() - 2);
        long curr = hist.get(hist.size() - 1);

        if (prev == 0)
            return curr > 0 ? "UP" : "STABLE";

        double changeRate = (double) (curr - prev) / prev;
        if (changeRate > 0.05)
            return "UP";
        if (changeRate < -0.05)
            return "DOWN";
        return "STABLE";
    }
}