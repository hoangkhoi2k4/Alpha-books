package com.alphabook.service;

import com.alphabook.dto.cart.CartItemRequest;
import com.alphabook.dto.cart.CartItemResponse;
import com.alphabook.dto.cart.CartResponse;
import com.alphabook.entity.Book;
import com.alphabook.entity.Cart;
import com.alphabook.entity.CartItem;
import com.alphabook.entity.User;
import com.alphabook.repository.BookRepository;
import com.alphabook.repository.CartItemRepository;
import com.alphabook.repository.CartRepository;
import com.alphabook.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Lấy giỏ hàng của user
    public CartResponse getCart(String email){
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);
        return convertToCartResponse(cart);
    }


    //Thêm sách vào giỏ hàng
    @Transactional
    public CartResponse addToCart(String email, CartItemRequest request){
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với id: " + request.getBookId()));

        Optional<CartItem> existingItem = cart.getListCartItem().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if(existingItem.isPresent()){
            // Có thì cộng thêm
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(request.getQuantity());
            cart.getListCartItem().add(newItem);
            cartItemRepository.save(newItem);
        }

        return convertToCartResponse(cartRepository.save(cart));
    }

    // Cập nhật giỏ hàng
    @Transactional
    public CartResponse updateCartItem(String email, Long cartItemId, Integer quantity){
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item: " + cartItemId));

        if(!cartItem.getCart().getId().equals(cart.getId())){
            throw new RuntimeException("Bạn không có quyền sửa item này");
        }

        if(quantity == 0){
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return convertToCartResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    // Xóa 1 item khỏi giỏ hàng
    @Transactional
    public CartResponse removeFromCart(String email, Long cartItemId){
        return updateCartItem(email, cartItemId, 0);
    }

    // Xóa toàn bộ giỏ hàng
    @Transactional
    public void clearCart(String email){
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteAll(cart.getListCartItem());
        cart.getListCartItem().clear();
        cartRepository.save(cart);
    }

    // Các hàm helper
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setListCartItem(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + email));
    }

    private CartResponse convertToCartResponse(Cart cart){
        List<CartItemResponse> items = cart.getListCartItem().stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());
        return new CartResponse(cart.getId(), items);
    }

    private CartItemResponse convertToCartItemResponse(CartItem item){
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setTitle(item.getBook().getTitle());
        response.setImageUrl(item.getBook().getImageUrl());
        response.setPrice(item.getBook().getPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }

}
