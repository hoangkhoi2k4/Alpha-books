package com.alphabook.service;

import com.alphabook.dto.order.OrderItemResponse;
import com.alphabook.dto.order.OrderRequest;
import com.alphabook.dto.order.OrderResponse;
import com.alphabook.entity.*;
import com.alphabook.enums.OrderStatus;
import com.alphabook.repository.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = getUserByEmail(email);
        Cart cart = getCartByUser(user);

        if (cart.getListCartItem() == null || cart.getListCartItem().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể đặt hàng");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getListCartItem()) {
            Book book = cartItem.getBook();
            Integer quantity = cartItem.getQuantity();

            if (book.getStock() == null || book.getStock() < quantity) {
                throw new RuntimeException("Sách '" + book.getTitle() + "' không đủ tồn kho");
            }

            BigDecimal itemPrice = book.getPrice();
            BigDecimal itemSubtotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(itemSubtotal);

            book.setStock(book.getStock() - quantity);
            bookRepository.save(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setPrice(itemPrice);
            orderItem.setQuantity(quantity);

            orderItems.add(orderItem);
        }

        order.setListOrderItem(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getListCartItem());
        cart.getListCartItem().clear();
        cartRepository.save(cart);

        return convertToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email){
        User user = getUserByEmail(email);
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long orderId) {
        User user = getUserByEmail(email);
        Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Order với id: " + orderId));
        if(!order.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Đơn hàng không thuộc về bạn!");
        }
        return convertToOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        User user = getUserByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Order với id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Đơn hàng không thuộc về bạn!");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Không thể huỷ đơn hàng ở trạng thái: " + order.getStatus());
        }
        
        if (order.getListOrderItem() != null) {
            for (OrderItem item : order.getListOrderItem()) {
                Book book = item.getBook();
                book.setStock(book.getStock() + item.getQuantity());
                bookRepository.save(book);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return convertToOrderResponse(savedOrder);
        
    }

    // các hàm helper
    private User getUserByEmail(String email){
        return userRepository.findByEmail(email).
                orElseThrow(() -> new RuntimeException("Không tìm thấy user có email: " + email));
    }

    private Cart getCartByUser(User user){
        return cartRepository.findByUser(user).
                orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getListOrderItem().stream()
                .map(item -> {
                    OrderItemResponse response = new OrderItemResponse();
                    response.setBookId(item.getBook().getId());
                    response.setTitle(item.getBook().getTitle());
                    response.setPrice(item.getPrice());
                    response.setQuantity(item.getQuantity());
                    return response;
                })
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress(),
                itemResponses
        );
    }
}
