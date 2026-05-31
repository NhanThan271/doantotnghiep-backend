package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.entity.CashierSession;
import java.math.BigDecimal;
import java.util.List;

public interface CashierSessionService {

    CashierSession openSession(OpenSessionRequest request);

    CashierSession closeSession(
            Long sessionId,
            CloseSessionRequest request);

    CashierSession getCurrentSession(Long staffId);

    List<CashierSession> getHistory();

    CashierSession getById(Long id);

    void updateRevenue(
            Long sessionId,
            BigDecimal amount,
            String paymentMethod);

}