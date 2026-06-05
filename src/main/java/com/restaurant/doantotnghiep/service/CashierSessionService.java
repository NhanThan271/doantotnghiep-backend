package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.BranchReportResponse;
import com.restaurant.doantotnghiep.dto.CashierReportResponse;
import com.restaurant.doantotnghiep.dto.CashierSessionResponse;
import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.entity.CashierSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CashierSessionService {

        CashierSessionResponse openSession(OpenSessionRequest request);

        CashierSession closeSession(
                        Long sessionId,
                        CloseSessionRequest request);

        CashierSessionResponse closeSessionAndReturnResponse(Long sessionId, CloseSessionRequest request);

        CashierSessionResponse getCurrentSession(Long staffId);

        List<CashierSession> getHistory();

        CashierSession getById(Long id);

        void updateRevenue(
                        Long sessionId,
                        BigDecimal amount,
                        String paymentMethod);

        CashierSessionResponse getByIdResponse(Long id);

        List<CashierSessionResponse> getHistoryResponse();

        List<CashierSessionResponse> getByBranch(Long branchId);

        CashierReportResponse getReport();

        CashierReportResponse getReportByDate(
                        LocalDateTime from,
                        LocalDateTime to);

        List<BranchReportResponse> getBranchReports();

        CashierSessionResponse getCurrentSessionByBranch(Long branchId);
}