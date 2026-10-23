package br.edu.ppi.seller.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

// Contrato do tópico order_notification, consumido pelo email-ms
// (br.edu.catolica.delivery.app.email.dto.OrderNotificationEvent). Os nomes dos campos precisam ser iguais.
@Builder
public record OrderNotificationDTO(
        String orderCode,
        String customerName,
        String customerEmail,
        String sellerName,
        List<OrderItemNotificationDTO> items,
        BigDecimal totalAmount
) {
}
