package com.alphabook.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private String shippingAddress;
    private List<OrderItemResponse> orderItemResponses;

}
