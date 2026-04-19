package com.SachinDev.order_service.service;

import com.SachinDev.order_service.dto.OrderLineItemsDto;
import com.SachinDev.order_service.dto.OrderRequest;
import com.SachinDev.order_service.model.Order;
import com.SachinDev.order_service.model.OrderLineItem;
import com.SachinDev.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest)
    {
        Order order =new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> items=  orderRequest.getOrderLineItems().stream().map(this::mapToDto).toList();
        order.setOrderLineItems(items);
        orderRepository.save(order);
    }

    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
    return orderLineItem;
    }
}
