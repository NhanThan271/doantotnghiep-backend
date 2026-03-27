package com.restaurant.doantotnghiep.entity.enums;

public enum OrderStatus {
    DRAFT, // khách đang chọn món
    PENDING, // khách gửi order
    CONFIRMED, // nhân viên nhận
    PREPARING, // bếp đang nấu
    COMPLETED, // bếp nấu xong
    SERVED, // nhân viên đã phục vụ
    PAID, // khách đã thanh toán
    CANCELED // đơn bị hủy
}
