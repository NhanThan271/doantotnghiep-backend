package com.restaurant.doantotnghiep.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.restaurant.doantotnghiep.dto.CashierSessionResponse;
import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.entity.CashierSession;
import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.CashierSessionStatus;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.CashierSessionRepository;
import com.restaurant.doantotnghiep.repository.ShiftRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.service.CashierSessionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashierSessionServiceImpl implements CashierSessionService {
        private final StaffRepository staffRepository;
        private final BranchRepository branchRepository;

        private final ShiftRepository shiftRepository;
        private final CashierSessionRepository cashierSessionRepository;

        @Override
        @Transactional //sửa chỗ này --
        public CashierSessionResponse openSession(OpenSessionRequest request) {

                Staff staff = staffRepository.findById(
                                request.getStaffId()).orElseThrow();

                if (cashierSessionRepository
                                .existsByStaffAndStatus(
                                                staff,
                                                CashierSessionStatus.OPEN)) {

                        throw new RuntimeException(
                                        "Bạn đang có ca chưa đóng");
                }

                Shift shift = null;

                if (request.getShiftId() != null) {
                        shift = shiftRepository.findById(
                                        request.getShiftId()).orElse(null);
                }

                CashierSession session = CashierSession.builder()
                                .staff(staff)
                                .branch(
                                                branchRepository
                                                                .findById(request.getBranchId())
                                                                .orElseThrow())
                                .shift(shift)
                                .openedAt(LocalDateTime.now())
                                .openingCash(request.getOpeningCash())
                                .cashRevenue(BigDecimal.ZERO)
                                .transferRevenue(BigDecimal.ZERO)
                                .totalRevenue(BigDecimal.ZERO)
                                .totalOrders(0)
                                .status(CashierSessionStatus.OPEN)
                                .build();

                CashierSession saved = cashierSessionRepository.save(session);

                return CashierSessionResponse.builder()
                                .id(saved.getId())
                                .staffId(saved.getStaff().getId())
                                .staffName(saved.getStaff().getUser().getFullName())
                                .branchId(saved.getBranch().getId())
                                .branchName(saved.getBranch().getName())
                                .openingCash(saved.getOpeningCash())
                                .cashRevenue(saved.getCashRevenue())
                                .transferRevenue(saved.getTransferRevenue())
                                .totalRevenue(saved.getTotalRevenue())
                                .totalOrders(saved.getTotalOrders())
                                .openedAt(saved.getOpenedAt())
                                .status(saved.getStatus())
                                .build();
        }

        @Override
        @Transactional
        public CashierSession closeSession(
                        Long sessionId,
                        CloseSessionRequest request) {

                CashierSession session = cashierSessionRepository
                                .findById(sessionId)
                                .orElseThrow();

                BigDecimal expectedCash = session.getOpeningCash()
                                .add(session.getCashRevenue());

                BigDecimal difference = request.getActualCash()
                                .subtract(expectedCash);

                session.setExpectedCash(expectedCash);
                session.setActualCash(request.getActualCash());
                session.setDifferenceAmount(difference);
                session.setClosedAt(LocalDateTime.now());
                session.setStatus(
                                CashierSessionStatus.CLOSED);

                session.setNote(request.getNote());

                return cashierSessionRepository.save(session);
        }

        @Override
        @Transactional
        public void updateRevenue(
                        Long sessionId,
                        BigDecimal amount,
                        String paymentMethod) {

                CashierSession session = cashierSessionRepository
                                .findById(sessionId)
                                .orElseThrow();

                if ("CASH".equals(paymentMethod)) {

                        session.setCashRevenue(
                                        session.getCashRevenue()
                                                        .add(amount));

                } else {

                        session.setTransferRevenue(
                                        session.getTransferRevenue()
                                                        .add(amount));
                }

                session.setTotalRevenue(
                                session.getTotalRevenue()
                                                .add(amount));

                session.setTotalOrders(
                                session.getTotalOrders() + 1);

                cashierSessionRepository.save(session);
        }

        @Override
        public CashierSessionResponse getCurrentSession(Long staffId) {

                Staff staff = staffRepository.findById(staffId)
                                .orElseThrow();

                CashierSession session = cashierSessionRepository
                                .findByStaffAndStatus(
                                                staff,
                                                CashierSessionStatus.OPEN)
                                .orElse(null);

                if (session == null) {
                        return null;
                }

                return CashierSessionResponse.builder()
                                .id(session.getId())
                                .staffId(session.getStaff().getId())
                                .staffName(session.getStaff().getUser().getFullName())
                                .branchId(session.getBranch().getId())
                                .branchName(session.getBranch().getName())
                                .openingCash(session.getOpeningCash())
                                .cashRevenue(session.getCashRevenue())
                                .transferRevenue(session.getTransferRevenue())
                                .totalRevenue(session.getTotalRevenue())
                                .totalOrders(session.getTotalOrders())
                                .openedAt(session.getOpenedAt())
                                .closedAt(session.getClosedAt())
                                .status(session.getStatus())
                                .build();
        }

        @Override
        public CashierSession getById(Long id) {
                return cashierSessionRepository.findById(id)
                                .orElseThrow();
        }

        @Override
        public java.util.List<CashierSession> getHistory() {
                return cashierSessionRepository.findAllByOrderByOpenedAtDesc();
        }

}
