package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WebClient.Builder webClient;

    public void placeOrder(OrderRequest payload){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        boolean res = false;

        List<OrderLineItem> orderLineItems = payload.getOrderLineItems()
                .stream()
                .map(this::mapToModel)
                .toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems().stream().
                map(orderLineItem -> orderLineItem.getSkuCode()).
                toList();

        // Call inventory service, and place order if Product is in stock
        InventoryResponse[] resArray = webClient.build().get().
                uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("sku_code", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (resArray.length > 0) {
            res = Arrays.stream(resArray)
                    .allMatch(inventoryResponse -> inventoryResponse.isInStock());
        }

        if (res) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again");
        }

    }

    private OrderLineItem mapToModel(OrderLineItemRequest orderLineItemRequest) {
        OrderLineItem orderLineItems = new OrderLineItem();
        orderLineItems.setQuantity(orderLineItemRequest.getQuantity());
        orderLineItems.setPrice(orderLineItemRequest.getPrice());
        orderLineItems.setSkuCode(orderLineItemRequest.getSkuCode());
        return orderLineItems;
    }
}
