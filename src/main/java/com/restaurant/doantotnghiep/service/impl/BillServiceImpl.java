package com.restaurant.doantotnghiep.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.restaurant.doantotnghiep.entity.Bill;
import com.restaurant.doantotnghiep.repository.BillRepository;
import com.restaurant.doantotnghiep.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bill> getAllBills() {
        List<Bill> bills = billRepository.findAll();

        // FORCE LOAD ALL RELATIONSHIPS
        bills.forEach(bill -> {
            if (bill.getOrder() != null) {
                bill.getOrder().getId();

                if (bill.getOrder().getItems() != null) {
                    bill.getOrder().getItems().size();
                    bill.getOrder().getItems().forEach(item -> {
                        if (item.getFood() != null) {
                            item.getFood().getName();
                            if (item.getFood().getCategory() != null) {
                                item.getFood().getCategory().getName();
                            }
                        }
                    });
                }

                if (bill.getOrder().getBranch() != null) {
                    bill.getOrder().getBranch().getName();
                }

                if (bill.getOrder().getTable() != null) {
                    bill.getOrder().getTable().getNumber();
                }

                if (bill.getOrder().getPromotion() != null) {
                    bill.getOrder().getPromotion().getName();
                }

                if (bill.getOrder().getEmployee() != null) {
                    bill.getOrder().getEmployee().getUsername();
                }
            }
        });

        return bills;
    }

    @Override
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id " + id));
    }

    @Override
    public Bill createBill(Bill bill) {
        bill.setCreatedAt(LocalDateTime.now());
        bill.setIssuedAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    @Override
    public Bill updateBill(Long id, Bill bill) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id " + id));

        existing.setOrder(bill.getOrder());
        existing.setTotalAmount(bill.getTotalAmount());
        existing.setPaymentMethod(bill.getPaymentMethod());
        existing.setPaymentStatus(bill.getPaymentStatus());
        existing.setNotes(bill.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());

        return billRepository.save(existing);
    }

    @Override
    public void deleteBill(Long id) {
        if (!billRepository.existsById(id)) {
            throw new RuntimeException("Bill not found with id " + id);
        }
        billRepository.deleteById(id);
    }

    @Override
    public byte[] exportBillToPdf(Long id) {
        Bill bill = getBillById(id);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN"));
            document.add(new Paragraph("Mã hóa đơn: " + bill.getId()));
            document.add(new Paragraph("Mã đơn hàng: " + bill.getOrder().getId()));
            document.add(new Paragraph("Tổng tiền: " + bill.getTotalAmount()));
            document.add(new Paragraph("Phương thức thanh toán: " + bill.getPaymentMethod()));
            document.add(new Paragraph("Trạng thái thanh toán: " + bill.getPaymentStatus()));
            document.add(new Paragraph("Ngày phát hành: " + bill.getIssuedAt()));
            document.add(new Paragraph("Ghi chú: " + bill.getNotes()));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất hóa đơn PDF: " + e.getMessage());
        }
    }

}
