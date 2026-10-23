package br.edu.catolica.delivery.app.email.messenger;

import br.edu.catolica.delivery.app.email.dto.OrderNotificationEvent;
import br.edu.catolica.delivery.app.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static br.edu.catolica.delivery.app.email.constants.TopicConstants.ORDER_NOTIFICATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationListener {

    private final EmailService emailService;

    @KafkaListener(topics = ORDER_NOTIFICATION, groupId = "${spring.kafka.consumer.group-id-email}")
    public void onOrderNotification(OrderNotificationEvent event) {
        try {
            emailService.sendOrderReceivedEmail(event);
        } catch (Exception e) {
            log.error("Falha ao processar notificação do pedido {}: {}", event.orderCode(), e.getMessage());
            throw e;
        }
    }
}
