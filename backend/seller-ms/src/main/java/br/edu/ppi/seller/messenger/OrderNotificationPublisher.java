package br.edu.ppi.seller.messenger;

import br.edu.ppi.seller.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static br.edu.ppi.seller.constants.TopicConstants.ORDER_NOTIFICATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderNotification(OrderNotificationDTO notificationDTO){
        try{
            kafkaTemplate.send(ORDER_NOTIFICATION, notificationDTO.orderCode(), notificationDTO);
        } catch (Exception e) {
            log.error("m=sendOrderNotification, error to send event to order notification topic, order code = {}",
                    notificationDTO.orderCode());
            throw new RuntimeException(e);
        }
    }

}
