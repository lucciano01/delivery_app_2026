package br.edu.catolica.customer_ms.messenger;

import br.edu.catolica.customer_ms.dto.OrderResponseDTO;
import br.edu.catolica.customer_ms.dto.OrderStatusUpdateDTO;
import br.edu.catolica.customer_ms.service.OrderEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static br.edu.catolica.customer_ms.constants.TopicConstants.ORDER_RESPONSE;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = ORDER_RESPONSE, groupId = "${spring.kafka.consumer.group-id}")
public class OrderResponseListener {

    private final OrderEventService orderEventService;

    @KafkaHandler
    public void orderResponse(OrderResponseDTO orderResponseDTO){
        try {
            orderEventService.applyResponse(orderResponseDTO);
        }catch (Exception ex){
            log.error("m=orderResponse, error to try consume event from order response = {}" , orderResponseDTO);
        }
    }

    @KafkaHandler
    public void updateResponse(OrderStatusUpdateDTO orderStatusUpdateDTO){
         orderEventService.applyOrderStatusUpdate(orderStatusUpdateDTO);
    }

}
