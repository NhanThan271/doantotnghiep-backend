package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.CashierSessionResponse;
import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.entity.CashierSession;
import java.math.BigDecimal;
import java.util.List;

public interface CashierSessionService {

        CashierSessionResponse openSession(OpenSessionRequest request);

        CashierSessionResponse closeSession(Long sessionId, CloseSessionRequest request);

        CashierSessionResponse getCurrentSession(Long staffId);

        List<CashierSession> getHistory();

        CashierSession getById(Long id);

        CashierSessionResponse getSessionResponse(Long id);

        void updateRevenue(Long sessionId, BigDecimal amount, String paymentMethod);
}