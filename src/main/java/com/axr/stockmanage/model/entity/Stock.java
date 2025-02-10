package com.axr.stockmanage.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Data
@Builder
public class Stock {
    private Long id;
    private Long productId;
    private Integer quantity;
}
