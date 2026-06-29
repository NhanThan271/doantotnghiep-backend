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
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = isWeekly ? to.minusWeeks(12) : to.minusMonths(6);
        // Dùng 0L thay null để tránh lỗi IS NULL trên PostgreSQL
        Long safeBranchId = (branchId != null) ? branchId : 0L;

        List<Object[]> rows = isWeekly
                ? orderItemRepository.findWeeklySalesByFood(from, to, safeBranchId)
                : orderItemRepository.findMonthlySalesByFood(from, to, safeBranchId);

        Map<Long, FoodForecastDTO> map = new LinkedHashMap<>();

        Set<String> allPeriods = new LinkedHashSet<>();
        Map<Long, Map<String, Long>> foodPeriodMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            if (row[0] == null)
                continue;
            Long foodId = ((Number) row[0]).longValue();
            String name = row[1] != null ? row[1].toString() : "";
            Long qty = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            String period = row[4] + "-" + row[3];

            allPeriods.add(period);
            foodPeriodMap.computeIfAbsent(foodId, id -> new LinkedHashMap<>());
            foodPeriodMap.get(foodId).put(period, qty);

            map.computeIfAbsent(foodId, id -> {
                FoodForecastDTO dto = new FoodForecastDTO();
                dto.setFoodId(id);
                dto.setFoodName(name);
                dto.setHistory(new ArrayList<>());
                dto.setTotalPast(0L);
                return dto;
            });

        }

        for (Map.Entry<Long, FoodForecastDTO> entry : map.entrySet()) {
            Long foodId = entry.getKey();
            FoodForecastDTO dto = entry.getValue();
            Map<String, Long> periodData = foodPeriodMap.get(foodId);

            long total = 0;
            List<Long> hist = new ArrayList<>();
            for (String period : allPeriods) {
                long qty = periodData.getOrDefault(period, 0L);
                hist.add(qty);
                total += qty;
            }
            dto.setHistory(hist);
            dto.setTotalPast(total);
        }

        map.values().forEach(dto -> {
            List<Long> hist = dto.getHistory();

            List<Long> completedHist = hist.size() > 1
                    ? hist.subList(0, hist.size() - 1)
                    : hist;

            double avg = completedHist.stream().mapToLong(Long::longValue).average().orElse(0);
            dto.setAvgPerPeriod(avg);
            dto.setForecastNextPeriod(weightedForecast(completedHist));
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

        if (prev == 0 && curr == 0)
            return "STABLE";
        if (prev == 0 && curr > 0)
            return "UP";
        if (prev > 0 && curr == 0)
            return "DOWN";

        double changeRate = (double) (curr - prev) / prev;
        if (changeRate > 0.05)
            return "UP";
        if (changeRate < -0.05)
            return "DOWN";
        return "STABLE";
    }
}