package br.edu.catolica.customer_ms.service;

import br.edu.catolica.customer_ms.constants.OrderConstants;
import br.edu.catolica.customer_ms.domain.Customer;
import br.edu.catolica.customer_ms.domain.ItemOrder;
import br.edu.catolica.customer_ms.domain.Order;
import br.edu.catolica.customer_ms.dto.*;
import br.edu.catolica.customer_ms.event.OrderEventPublisher;
import br.edu.catolica.customer_ms.exception.CustomerException;
import br.edu.catolica.customer_ms.exception.OrderNotFoundException;
import br.edu.catolica.customer_ms.repositories.CustomerRepository;
import br.edu.catolica.customer_ms.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.edu.catolica.customer_ms.constants.OrderConstants.ORDER_MESSAGE_204;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventService {

    private  final OrderServicePersistence orderServicePersistence;
    private final CustomerRepository customerRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderRepository orderRepository;

    public void send(OrderRequestDTO orderRequestDTO){

        Customer customer = customerRepository
                .findById(orderRequestDTO.customerId())
                .orElseThrow(() ->{
                   log.warn("m=send, customer not found to id = {}",
                           orderRequestDTO.customerId());
                 return new CustomerException("Customer não encontrado");
                });
        OrderRequestDTO request = withOrderCode(orderRequestDTO);
        orderServicePersistence.saveOrder(request);

        OrderCreatedEventDTO eventDTO = OrderCreatedEventDTO.builder()
                .customerEmail(customer.getEmail())
                .customerId(customer.getId())
                .customerName(customer.getName())
                .items(request.items())
                .orderCode(request.orderCode())
                .sellerId(request.sellerId())
                .build();
     try {
        orderEventPublisher.sendOrderRequestEvent(eventDTO);
    } catch (Exception e) {
        log.error("m=send, fail try send order to topic," +
                " order code = {}", eventDTO.orderCode());
        orderServicePersistence.updateOrderToFailed(eventDTO.orderCode());
        throw new RuntimeException(e);
    }
    }

    private OrderRequestDTO withOrderCode(OrderRequestDTO orderRequestDTO){
        if(Strings.isNotBlank(orderRequestDTO.orderCode())){
            return orderRequestDTO;
        }
        return new OrderRequestDTO(orderRequestDTO.sellerId(),
                orderRequestDTO.customerId(),
                orderRequestDTO.items()
        );
    }

    @Transactional
    public void applyResponse(OrderResponseDTO orderResponseDTO){

        if(StringUtils.isBlank(orderResponseDTO.orderCode())){
            log.error("m=applyResponse, invalid order code");
            return;
        }
        Order order = findOrderByCode(orderResponseDTO.orderCode());

        List<ItemOrder> items = orderResponseDTO.items()
                .stream().map(itemDTO ->
                    toItemOrder(itemDTO, order)).toList();

        order.getItems().clear();
        order.getItems().addAll(items);
        order.setAmount(orderResponseDTO.amount());
        order.setStatus(orderResponseDTO.orderStatus());
        // dirty checking
        orderRepository.save(order);
    }

    public void applyOrderStatusUpdate(OrderStatusUpdateDTO orderStatusUpdateDTO){

        if(orderStatusUpdateDTO.orderCode().isBlank()){
            log.error("m=applyOrderStatusUpdate, invalid order code");
            return;
        }

        Order order = orderRepository.findByOrderCode(orderStatusUpdateDTO.orderCode())
                .orElseThrow(() -> {
                    System.out.println("teste");
                    log.warn("m=applyOrderStatusUpdate, order not found to order code = {} ",orderStatusUpdateDTO.orderCode());
                    return new OrderNotFoundException(ORDER_MESSAGE_204);
                });
        order.setStatus(orderStatusUpdateDTO.orderStatus());
        orderRepository.save(order);

    }

    public Order findOrderByCode(String orderCode){
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> {
                    log.warn("m=findOrderByCode order not found by order code = {} ", orderCode);
                    return new OrderNotFoundException("Pedido não encontrado");
                });
    }

    private ItemOrder toItemOrder(ItemOrderDTO itemOrderDTO, Order order){
        return ItemOrder.builder()
                .productId(itemOrderDTO.productDTO().id())
                .quantity(itemOrderDTO.quantity())
                .subAmount(itemOrderDTO.subAmount())
                .order(order)
                .build();
    }



}
