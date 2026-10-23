package br.edu.ppi.seller.service;

import br.edu.ppi.seller.constants.OrderConstants;
import br.edu.ppi.seller.domain.Order;
import br.edu.ppi.seller.domain.Product;
import br.edu.ppi.seller.domain.Seller;
import br.edu.ppi.seller.dto.*;
import br.edu.ppi.seller.enums.OrderStatus;
import br.edu.ppi.seller.exception.OrderNotFoundException;
import br.edu.ppi.seller.exception.ProductException;
import br.edu.ppi.seller.exception.SellerException;
import br.edu.ppi.seller.mapper.OrderMapper;
import br.edu.ppi.seller.messenger.OrderNotificationPublisher;
import br.edu.ppi.seller.messenger.OrderSendResponseStatus;
import br.edu.ppi.seller.repository.OrderRepository;
import br.edu.ppi.seller.repository.ProductRepository;
import br.edu.ppi.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static br.edu.ppi.seller.constants.OrderConstants.ORDER_MESSAGE_204;
import static br.edu.ppi.seller.constants.ProductConstants.PRODUCT_MESSAGE_204;
import static br.edu.ppi.seller.constants.SellerConstants.SELLER_MESSAGE_204;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final OrderSendResponseStatus orderSendResponseStatus;
    private final OrderNotificationPublisher orderNotificationPublisher;

     @Transactional
    public void saveOrder(OrderCreatedEventDTO eventDTO){

        if(orderRepository.existsByOrderCode(eventDTO.orderCode())){
            log.warn("m=saveOrder order already with order code = {}",
                    eventDTO.orderCode());
        }
        Order order = orderMapper.orderEventDtoToEntity(eventDTO);

        Seller seller = findSellerById(order);

        List<ItemOrderDTO> items =  buildItemsOrderDTO(order);

        BigDecimal amount = calculateOrderTotalAmount(items);

        order.setAmount(amount);

        order.setOrderStatus(OrderStatus.ACCEPTED);

        orderRepository.save(order);

        orderSendResponseStatus
                .sendResponseOrderStatus(orderMapper
                        .toResponseDTO(order, seller, items));

        if(order.getOrderStatus().equals(OrderStatus.ACCEPTED)){
            orderNotificationPublisher
                    .sendOrderNotification(buildOrderNotification(eventDTO, seller, items, amount));
        }

    }

    public void updateOrderStatus(OrderStatusUpdateDTO orderStatusUpdateDTO){
         Order order = orderRepository.findByOrderCode(orderStatusUpdateDTO.orderCode())
                 .orElseThrow(() ->{
                     log.warn("m=updateOrderStatus, order not found to order code = {} ", orderStatusUpdateDTO.orderCode());
                     return new OrderNotFoundException(ORDER_MESSAGE_204);
                 });
         order.setOrderStatus(orderStatusUpdateDTO.orderStatus());
         orderRepository.save(order);
         orderSendResponseStatus.sendOrderStatusUpdated(orderStatusUpdateDTO);

    }

    private List<ItemOrderDTO> buildItemsOrderDTO(Order order){
        return order.getItems()
                .stream().map(itemOrder -> {
                    Product product = productRepository.findById(itemOrder.getProductId())
                            .orElseThrow(() -> {
                                log.warn("m=buildItemsOrderDTO, product not found, id = " +
                                        "{}", itemOrder.getProductId());
                                return new ProductException(PRODUCT_MESSAGE_204);
                            });
                    itemOrder.setOrder(order);
                    itemOrder.setSubAmount(product.getPrice()
                            .multiply(BigDecimal.valueOf(itemOrder.getQuantity())));
                    return orderMapper.toItemOrderDTO(itemOrder, product);
                }).toList();
    }

    private OrderNotificationDTO buildOrderNotification(OrderCreatedEventDTO eventDTO, Seller seller,
                                                        List<ItemOrderDTO> items, BigDecimal amount){
        return OrderNotificationDTO.builder()
                .orderCode(eventDTO.orderCode())
                .customerName(eventDTO.customerName())
                .customerEmail(eventDTO.customerEmail())
                .sellerName(seller.getName())
                .items(items.stream().map(item -> new OrderItemNotificationDTO(
                        item.productDTO().description(),
                        item.quantity().intValue(),
                        item.subAmount())).toList())
                .totalAmount(amount)
                .build();
    }

    private BigDecimal calculateOrderTotalAmount(List<ItemOrderDTO> items){
        return items.stream().map(ItemOrderDTO::subAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Seller findSellerById(Order order){
        return sellerRepository.findById(order.getSellerId())
                .orElseThrow(() -> {
                    log.warn("m=findSellerById, seller not found to id = {} ", order.getSellerId());
                    return new SellerException(SELLER_MESSAGE_204);
                });
    }

}
