package br.edu.catolica.delivery.app.email;

import br.edu.catolica.delivery.app.email.dto.OrderItemNotificationDTO;
import br.edu.catolica.delivery.app.email.dto.OrderNotificationEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    static void main() {

        List<OrderItemNotificationDTO> items = new ArrayList<>();
        items.add(new OrderItemNotificationDTO("Mc Fritas G", 2, new BigDecimal(50)));
        items.add(new OrderItemNotificationDTO("Mc Chicken", 1, new BigDecimal(30)));
        items.add(new OrderItemNotificationDTO("Coca-Cola 2L", 1, new BigDecimal(20)));
        items.add(new OrderItemNotificationDTO("Milk Shake Chocolate", 1, new BigDecimal("22.5")));

        OrderNotificationEvent ev =
                new OrderNotificationEvent("59baf6e2-8792-4cde-ae1e-438961111b99",
                        "Neymar Jr.",
                        "njr@email.com",
                        "Mc Donald's",
                        items,
                        new BigDecimal("122.5")
                );

        System.out.println(buildBody(ev));

    }

    static String buildBody(OrderNotificationEvent event) {
        StringBuilder body = new StringBuilder();
        body.append("Olá, ").append(event.customerName()).append("!\n\n");
        body.append("Seu pedido foi recebido pelo estabelecimento ").append(event.sellerName()).append(".\n\n");
        body.append("Itens do pedido:\n");

        for (OrderItemNotificationDTO item : event.items()) {
            body.append("- ").append(item.productName())
                    .append(" (x").append(item.quantity()).append(")")
                    .append(" - R$ ").append(item.subAmount())
                    .append("\n");
        }

        body.append("\nTotal do pedido: R$ ").append(event.totalAmount());
        return body.toString();
    }


}
