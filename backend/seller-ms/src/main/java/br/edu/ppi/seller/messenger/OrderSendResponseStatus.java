package br.edu.ppi.seller.messenger;

import br.edu.ppi.seller.dto.OrderResponseDTO;
import br.edu.ppi.seller.dto.OrderStatusUpdateDTO;
import br.edu.ppi.seller.exception.OrderSendResponseException;
import br.edu.ppi.seller.exception.OrderSendUpdateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static br.edu.ppi.seller.constants.TopicConstants.ORDER_RESPONSE;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderSendResponseStatus {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendResponseOrderStatus(OrderResponseDTO orderResponseDTO){
        try{
            kafkaTemplate.send(ORDER_RESPONSE, orderResponseDTO.orderCode(), orderResponseDTO);
        } catch (Exception e) {
            log.error("m=sendResponseOrderStatus, error to send event to order response topic");
            throw new OrderSendResponseException(e.getMessage());
        }
    }

    public void sendOrderStatusUpdated(OrderStatusUpdateDTO orderStatusUpdateDTO){
       try{
           kafkaTemplate.send(ORDER_RESPONSE, orderStatusUpdateDTO.orderCode(), orderStatusUpdateDTO);
       } catch (Exception e) {
           log.error("m=sendOrderStatusUpdated, error to try send update order status to order code = {} ", orderStatusUpdateDTO.orderCode() );
           throw new OrderSendUpdateException(e.getMessage());
       }

    }

}
