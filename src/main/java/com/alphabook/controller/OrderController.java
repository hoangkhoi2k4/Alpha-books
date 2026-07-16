package com.alphabook.controller;

import com.alphabook.dto.order.OrderRequest;
import com.alphabook.dto.order.OrderResponse;
import com.alphabook.service.OrderService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrder(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(orderService.getMyOrders(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(orderService.placeOrder(userDetails.getUsername(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId
    )
    {
        return ResponseEntity.ok(
                orderService.getOrderById(
                        userDetails.getUsername()
                        , orderId
                ));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId
    )
    {
        return ResponseEntity.ok(
                orderService.cancelOrder(
                        userDetails.getUsername(),
                        orderId
                ));
    }
}
