package br.edu.catolica.delivery.app.email.dto;

import java.math.BigDecimal;
import java.util.List;

// Espelha br.edu.ppi.seller.dto.OrderNotificationDTO, publicado no
// tópico order_notification pelo seller-ms. Precisa ter os mesmos nomes de campo,
// já que a desserialização ignora o header de tipo (spring.json.use.type.headers: false).
public record OrderNotificationEvent(
        String orderCode,
        String customerName,
        String customerEmail,
        String sellerName,
        List<OrderItemNotificationDTO> items,
        BigDecimal totalAmount
) {
}
