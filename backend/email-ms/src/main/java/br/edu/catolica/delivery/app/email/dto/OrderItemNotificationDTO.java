package br.edu.catolica.delivery.app.email.dto;

import java.math.BigDecimal;

public record OrderItemNotificationDTO(
        String productName,
        Integer quantity,
        BigDecimal subAmount
) {
}
