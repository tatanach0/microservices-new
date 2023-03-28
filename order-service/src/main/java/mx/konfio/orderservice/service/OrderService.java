package mx.konfio.orderservice.service;

import lombok.RequiredArgsConstructor;
import mx.konfio.orderservice.dto.InventoryResponse;
import mx.konfio.orderservice.dto.OrderLineItemsDto;
import mx.konfio.orderservice.dto.OrderRequest;
import mx.konfio.orderservice.model.Order;
import mx.konfio.orderservice.model.OrderLineItems;
import mx.konfio.orderservice.repository.OrderRepository;
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
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapFromDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(orderLineItems1 -> orderLineItems1.getSkuCode()).toList();

        // Call inventory service and place order if product is in stock
        //stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                        .build())
                .retrieve().bodyToMono(InventoryResponse[].class).block();

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.isInStock());

        if (allProductsInStock)
            orderRepository.save(order);
        else
            throw new IllegalArgumentException("Product is not in stock, please try later");
    }

    private OrderLineItems mapFromDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .skuCode(orderLineItemsDto.getSkuCode())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }
}
