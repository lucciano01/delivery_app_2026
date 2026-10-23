package br.edu.ppi.seller.dto;

import java.math.BigDecimal;

public record OrderItemNotificationDTO(
        String productName,
        Integer quantity,
        BigDecimal subAmount
) {
}
