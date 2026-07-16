package com.alphabook.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class OrderItemResponse {
    private Long bookId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        if (this.price == null || this.quantity == null) return BigDecimal.ZERO;
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}
