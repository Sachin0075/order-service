package com.SachinDev.order_service.service;

import com.SachinDev.order_service.dto.InventoryResponse;
import com.SachinDev.order_service.dto.OrderLineItemsDto;
import com.SachinDev.order_service.dto.OrderRequest;
import com.SachinDev.order_service.model.Order;
import com.SachinDev.order_service.model.OrderLineItem;
import com.SachinDev.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest)
    {
        Order order =new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> items=  orderRequest.getOrderLineItems().stream().map(this::mapToDto).toList();
        order.setOrderLineItems(items);

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode).toList();

        //call inventory service and if it exists then place order
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                                uriBuilder->uriBuilder.queryParam("skuCode", skuCodes)
                        .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);
        if(allProductInStock)
        {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock");
        }
    }

    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
    return orderLineItem;
    }
}
