package com.alphabook.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CartItemResponse {
    private Long cartItemId;
    private Long bookId;
    private String title, imageUrl;
    private BigDecimal price;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        if (this.price == null || this.quantity == null) return BigDecimal.ZERO;
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}
