package com.restaurant.doantotnghiep.mapper;

import com.restaurant.doantotnghiep.dto.OrderStatusDTO;
import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

        public static OrderStatusDTO toStatusDTO(Order order) {
                if (order == null) {
                        return null;
                }

                return OrderStatusDTO.builder()
                                .id(order.getId())
                                .customerName(order.getCustomerName())
                                .status(order.getStatus())
                                .totalAmount(order.getTotalAmount())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .table(order.getTable() != null ? OrderStatusDTO.TableDTO.builder()
                                                .id(order.getTable().getId())
                                                .number(order.getTable().getNumber())
                                                .status(order.getTable().getStatus() != null
                                                                ? order.getTable().getStatus().name()
                                                                : null)
                                                .build()
                                                : null)
                                .promotion(order.getPromotion() != null ? OrderStatusDTO.PromotionDTO.builder()
                                                .id(order.getPromotion().getId())
                                                .name(order.getPromotion().getName())
                                                .discountPercentage(order.getPromotion().getDiscountPercentage())
                                                .discountAmount(order.getPromotion().getDiscountAmount())
                                                .build()
                                                : null)
                                .items(order.getItems() != null ? order.getItems().stream()
                                                .map(OrderMapper::toItemDTO)
                                                .collect(Collectors.toList())
                                                : null)
                                .build();
        }

        private static OrderStatusDTO.OrderItemDTO toItemDTO(OrderItem item) {
                if (item == null) {
                        return null;
                }

                return OrderStatusDTO.OrderItemDTO.builder()
                                .id(item.getId())
                                .foodId(item.getFood() != null ? item.getFood().getId() : null)
                                .foodName(item.getFood() != null ? item.getFood().getName() : null)
                                .foodImageUrl(item.getFood() != null ? item.getFood().getImageUrl() : null)
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .build();
        }
}